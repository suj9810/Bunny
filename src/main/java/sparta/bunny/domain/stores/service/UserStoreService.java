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
import sparta.bunny.domain.stores.dto.response.StoreResponseDto;
import sparta.bunny.domain.stores.dto.response.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.exception.StoreException;
import sparta.bunny.domain.stores.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class UserStoreService {

	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;

	// 전체 가게 조회(사용자 계정이므로 폐업된 가게는 조회하지 않음)
	@Transactional(readOnly = true)
	public Page<StoreResponseDto> getStores(String categoryName, Pageable pageable) {
		Page<Store> stores;
		if (categoryName != null && !categoryName.isEmpty()) { // 카테고리가 있으면 카테고리별로 전체 가게가 조회
			stores = storeRepository.findAllByCategoryNameAndIsClosedFalse(categoryName, pageable);
		} else { // 카테고리가 없으면 모든 가게가 조회
			stores = storeRepository.findAllByIsClosedFalse(pageable);
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

	// 단일 가게 조회
	@Transactional(readOnly = true)
	public StoreWithMenuResponseDto getStoreWithMenus(Long storeId) {
		Store store = storeRepository.findById(storeId) // storeId로 가게 조회
			.orElseThrow(
				() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND)); // 가게가 존재하지 않으면 STORE_NOT_FOUND 예외 발생

		if (store.getIsClosed()) { // 가게 폐업 여부
			throw new StoreException(StoreExceptionCode.STORE_NOT_FOUND); // 가게가 폐업한 경우 STORE_NOT_FOUND 예외 발생
		}

		// 메뉴 조회
		List<Menu> menus = menuRepository.findAllByStoreId(storeId);

		List<MenuResponse> menuResponses = menus.stream()
			.map(menu -> {
				List<MenuOptionResponse> options = menu.getOptions().stream()
					.map(MenuOptionResponse::of)
					.collect(Collectors.toList());

				// 메뉴 이미지 URL, 이미지가 여러개 있을 시 첫 번째 이미지로 가져옴)
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
