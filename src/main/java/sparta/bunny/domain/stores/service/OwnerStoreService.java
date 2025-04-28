package sparta.bunny.domain.stores.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.menu.dto.response.MenuOptionResponse;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.code.StoreExceptionCode;
import sparta.bunny.domain.stores.dto.request.StoreRequestDto;
import sparta.bunny.domain.stores.dto.response.StoreResponseDto;
import sparta.bunny.domain.stores.dto.response.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.exception.StoreException;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;

@Service
@RequiredArgsConstructor
public class OwnerStoreService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;

	// 가게 등록
	@Transactional
	public void createStore(StoreRequestDto requestDto, User user) {

		// 사용자가 등록한 가게 개수 확인
		long userStoreCount = storeRepository.countByUser(user);

		// 가게 개수가 3개 이상이면 예외 처리
		if (userStoreCount >= 3) {
			throw new StoreException(StoreExceptionCode.STORE_LIMIT_EXCEEDED); // 예외 코드 추가
		}

		Store store = Store.builder()
			.user(user)
			.storeName(requestDto.getStoreName())
			.openTime(requestDto.getOpenTime())
			.closeTime(requestDto.getCloseTime())
			.minOrderPrice(requestDto.getMinOrderPrice())
			.isClosed(false) // 처음 등록할땐 폐업 아니라고 체크
			.categoryName(requestDto.getCategoryName())
			.build();
		storeRepository.save(store);
	}

	// 가게 수정
	@Transactional
	public void updateStore(Long storeId, StoreRequestDto requestDto, User user) {
		Store store = storeRepository.findById(storeId) // storeId로 가게 조회
			.orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND)); // 가게가 없으면 STORE_NOT_FOUND 예외 발생

		// 로그인한 사용자랑 가게 주인이 다르면 수정 불가
		if (!store.getUser().getId().equals(user.getId())) { // 본인 가게인지 확인
			throw new StoreException(StoreExceptionCode.UNAUTHORIZED_ACCESS); // owner가 아닐경우엔 UNAUTHORIZED_ACCESS 예외 발생
		}
		store.updateStore(
			requestDto.getStoreName(),
			requestDto.getOpenTime(),
			requestDto.getCloseTime(),
			requestDto.getMinOrderPrice(),
			requestDto.getNotice(),
			requestDto.getCategoryName()
		);
	}

	//가게 폐업/ 폐업 해제 처리
	@Transactional
	public void closeStore(Long storeId, boolean closure, User user) {
		Store store = storeRepository.findById(storeId) // storeId로 가게 조회
			.orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND)); // 가게가 없으면 STORE_NOT_FOUND 예외 발생

		// 로그인한 사용자와 가게 주인이 다르면 폐업 처리 불가
		if (!store.getUser().getId().equals(user.getId())) {
			throw new StoreException(
				StoreExceptionCode.UNAUTHORIZED_ACCESS); // 본인 가게인지 확인 본인 가게가 아니면 UNAUTHORIZED_ACCESS 예외 발생
		}

		if (closure) { // closure = true면 폐업 처리
			store.close();
		} else { // closure = false면 폐업 해제
			store.reopen();
		}
		storeRepository.save(store);
	}

	// 전체 가게 조회 (카테고리가 있으면 카테고리별로 전체 조회 없으면 가게 전체 조회)
	@Transactional(readOnly = true)
	public Page<StoreResponseDto> getStoresByCategory(User user, String categoryName, Pageable pageable) {
		Page<Store> stores;
		if (categoryName != null && !categoryName.isBlank()) { // 카테고리가 있을 때 카테고리별로 조회
			stores = storeRepository.findAllByUserAndCategoryName(user, categoryName, pageable);
		} else { // 카테고리가 없을 때 전체 조회
			stores = storeRepository.findAllByUser(user, pageable);
		}

		return stores.map(store -> new StoreResponseDto(
			store.getId(),
			store.getStoreName(),
			store.getOpenTime(),
			store.getCloseTime(),
			store.getMinOrderPrice(),
			store.getNotice(),
			store.getIsClosed(),
			store.getCategoryName()
		));
	}

	// 단일 가게 조회(메뉴 포함)
	@Transactional(readOnly = true)
	public StoreWithMenuResponseDto getStoreWithMenus(Long storeId, User user) {
		Store store = storeRepository.findById(storeId) // storeId로 가게 조회
			.orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND)); // 가게가 없으면 STORE_NOT_FOUND 예외 발생

		// 사장 본인의 가게인지 확인
		if (!store.getUser().getId().equals(user.getId())) {
			throw new StoreException(
				StoreExceptionCode.UNAUTHORIZED_ACCESS); // 본인 가게인지 확인 본인 가게가 아니면 UNAUTHORIZED_ACCESS 예외 발생
		}

		List<Menu> menus = menuRepository.findMenusWithOptionsAndImagesByStoreId(storeId); // 해당 가게에 저장된 매뉴 조회

		List<MenuResponse> menuResponses = menus.stream()
			.map(menu -> {
				List<MenuOptionResponse> options = menu.getOptions().stream()
					.map(MenuOptionResponse::of)
					.collect(Collectors.toList());

				// 메뉴 이미지 URL, 이미지가 여러개 있을 시 첫 번째 이미지로 가져옴
				String imageUrl = menu.getImages().isEmpty() ? null : menu.getImages().get(0).getImgUrl();

				return MenuResponse.builder()
					.menuId(menu.getId())
					.name(menu.getName())
					.description(menu.getDescription())
					.price(menu.getPrice())
					.status(menu.getStatus().name())
					.imageUrl(imageUrl)
					.store(null)
					.options(options)
					.build();
			})
			.collect(Collectors.toList());

		return StoreWithMenuResponseDto.builder()
			.id(store.getId())
			.storeName(store.getStoreName())
			.openTime(store.getOpenTime())
			.closeTime(store.getCloseTime())
			.minOrderPrice(store.getMinOrderPrice())
			.notice(store.getNotice())
			.isClosed(store.getIsClosed())
			.categoryName(store.getCategoryName())
			.menu(menuResponses)
			.build();
	}
}
