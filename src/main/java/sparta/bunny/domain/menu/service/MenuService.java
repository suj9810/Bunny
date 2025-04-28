package sparta.bunny.domain.menu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.service.FileService;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.menu.code.MenuExceptionCode;
import sparta.bunny.domain.menu.code.MenuSuccessCode;
import sparta.bunny.domain.menu.dto.request.MenuCreateRequest;
import sparta.bunny.domain.menu.dto.request.MenuUpdateRequest;
import sparta.bunny.domain.menu.dto.response.MenuOptionResponse;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.menu.dto.response.StoreInfoResponse;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.entity.MenuImage;
import sparta.bunny.domain.menu.entity.MenuOption;
import sparta.bunny.domain.menu.enums.Status;
import sparta.bunny.domain.menu.exception.MenuException;
import sparta.bunny.domain.menu.repository.MenuImageRepository;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.repository.StoreRepository;

/**
 * 메뉴 서비스
 */
@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	private final MenuImageRepository menuImageRepository;
	private final FileService fileService;

	/**
	 * 메뉴 생성
	 *
	 * @param request the request
	 * @param loginUserId the login user id
	 * @return the common response
	 * @throws IOException the io exception
	 */
	@Transactional
	public CommonResponse<MenuResponse> saveMenu(MenuCreateRequest request, Long loginUserId) throws IOException {

		Store store = storeRepository.findById(request.getStoreId())
			.orElseThrow(() -> new MenuException(MenuExceptionCode.NOT_FOUND_STORE));

		validOwner(store.getUser().getId(), loginUserId);

		Menu menu = Menu.builder()
			.store(store)
			.description(request.getDescription())
			.name(request.getName())
			.price(request.getPrice())
			.status(Status.ACTIVE)
			.build();

		List<MenuOption> options = request.getOptions().stream()
			.map(optionRequest -> MenuOption.builder()
				.name(optionRequest.getName())
				.price(optionRequest.getPrice())
				.menu(menu)
				.build())
			.toList();

		menu.getOptions().addAll(options);
		Menu save = menuRepository.save(menu);

		List<MenuImage> menuImages = fileService.uploadAndCreateEntities(
			request.getFiles(),
			"menu-images",
			url -> MenuImage.builder()
				.imgUrl(url)
				.menu(save)
				.build()
		);

		if (menuImages.isEmpty()) {
			throw new MenuException(MenuExceptionCode.MENU_IMAGE_UPLOAD_FAILED);
		}

		menuImageRepository.saveAll(menuImages);

		MenuResponse response = MenuResponse.builder()
			.menuId(menu.getId())
			.description(menu.getDescription())
			.name(menu.getName())
			.price(menu.getPrice())
			.status(menu.getStatus().name())
			.store(StoreInfoResponse.of(menu.getStore()))
			.imageUrl(menuImages.get(0).getImgUrl())
			.options(menu.getOptions().stream().map(MenuOptionResponse::of).toList())
			.build();

		return CommonResponse.of(MenuSuccessCode.MENU_CREATE_SUCCESS, response);
	}

	/**
	 * 메뉴 수정
	 *
	 * @param menuId the menu id
	 * @param userDetails the user details
	 * @param request the request
	 * @return the common response
	 * @throws IOException the io exception
	 */
	@Transactional
	public CommonResponse<MenuResponse> updateMenu(Long menuId, UserDetailsImpl userDetails,
		MenuUpdateRequest request) throws IOException {

		Menu menu = findMenu(menuId, userDetails.getUser().getId());

		List<MenuImage> menuImages = fileService.uploadAndCreateEntities(
			request.getFiles(),
			"menu-images",
			url -> MenuImage.builder()
				.imgUrl(url)
				.menu(menu)
				.build()
		);

		if (menuImages.isEmpty()) {
			throw new MenuException(MenuExceptionCode.MENU_IMAGE_UPLOAD_FAILED);
		}

		menu.updateMenu(request);
		MenuResponse response = MenuResponse.builder()
			.menuId(menu.getId())
			.description(menu.getDescription())
			.name(menu.getName())
			.price(menu.getPrice())
			.status(menu.getStatus().name())
			.imageUrl(menuImages.get(0).getImgUrl())
			.store(StoreInfoResponse.of(menu.getStore()))
			.options(menu.getOptions().stream().map(MenuOptionResponse::of).toList())
			.build();

		return CommonResponse.of(MenuSuccessCode.MENU_SUCCESS, response);
	}

	/**
	 * 메뉴 삭제 (softDelete)
	 *
	 * @param menuId the menu id
	 * @param userDetails the user details
	 * @return the common response
	 */
	@Transactional
	public CommonResponse<MenuResponse> deleteMenu(Long menuId, UserDetailsImpl userDetails) {

		Menu menu = findMenu(menuId, userDetails.getUser().getId());

		menu.changeStatus(Status.DELETED);
		return CommonResponse.of(MenuSuccessCode.MENU_NO_CONTENT, null);
	}

	/**
	 * 메뉴 ID 검증
	 * @param menuId
	 * @param loginUserId
	 * @return
	 */
	private Menu findMenu(Long menuId, Long loginUserId) {
		Menu menu = menuRepository.findById(menuId)
			.orElseThrow(() -> new MenuException(MenuExceptionCode.NOT_FOUND_MENU));

		validOwner(menu.getStore().getUser().getId(), loginUserId);

		return menu;
	}

	/**
	 * 오너 ID, 로그인 ID 검증
	 * @param ownerId
	 * @param loginUserId
	 */
	private void validOwner(Long ownerId, Long loginUserId) {
		if (!ownerId.equals(loginUserId)) {
			throw new MenuException(MenuExceptionCode.NOT_OWNER_OF_STORE);
		}
	}
}
