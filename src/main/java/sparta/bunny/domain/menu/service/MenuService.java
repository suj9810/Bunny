package sparta.bunny.domain.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.menu.code.MenuExceptionCode;
import sparta.bunny.domain.menu.code.MenuSuccessCode;
import sparta.bunny.domain.menu.dto.request.MenuCreateRequest;
import sparta.bunny.domain.menu.dto.response.MenuCreateResponse;
import sparta.bunny.domain.menu.dto.response.MenuOptionResponse;
import sparta.bunny.domain.menu.dto.response.StoreInfoResponse;
import sparta.bunny.domain.menu.entity.MenuOption;
import sparta.bunny.domain.menu.entity.Menus;
import sparta.bunny.domain.menu.enums.Status;
import sparta.bunny.domain.menu.exception.MenuException;
import sparta.bunny.domain.menu.repository.MenuOptionRepository;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.entity.Stores;
import sparta.bunny.domain.stores.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final MenuOptionRepository menuOptionRepository;
	private final StoreRepository storeRepository;

	@Transactional
	public CommonResponse<MenuCreateResponse> saveMenu(MenuCreateRequest request) {

		Stores store = storeRepository.findById(request.getStoreId())
			.orElseThrow(() -> new MenuException(MenuExceptionCode.NOT_FOUND_STORE));

		Menus menus = Menus.builder()
			.store(store)
			.description(request.getDescription())
			.name(request.getName())
			.price(request.getPrice())
			.imageUrl(request.getImageUrl())
			.status(Status.ACTIVE)
			.build();

		List<MenuOption> options = request.getOptions().stream()
			.map(optionRequest -> MenuOption.builder()
				.name(optionRequest.getName())
				.price(optionRequest.getPrice())
				.menus(menus)
				.build())
			.toList();

		menus.getOptions().addAll(options);
		menuRepository.save(menus);

		MenuCreateResponse response = MenuCreateResponse.builder()
			.menuId(menus.getId())
			.description(menus.getDescription())
			.name(menus.getName())
			.price(menus.getPrice())
			.imageUrl(menus.getImageUrl())
			.status(menus.getStatus().name())
			.store(StoreInfoResponse.of(menus.getStore()))
			.options(menus.getOptions().stream().map(MenuOptionResponse::of)
				.toList()
			)
			.build();
		return CommonResponse.of(MenuSuccessCode.MENU_CREATE_SUCCESS, response);
	}
}
