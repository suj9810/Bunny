package sparta.bunny.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.menu.dto.request.MenuCreateRequest;
import sparta.bunny.domain.menu.dto.request.MenuUpdateRequest;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.menu.service.MenuService;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	@PostMapping
	public ResponseEntity<CommonResponse<MenuResponse>> addMenu(
		@Valid @RequestBody MenuCreateRequest request) {
		CommonResponse<MenuResponse> response = menuService.saveMenu(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{menuId}")
	public ResponseEntity<CommonResponse<MenuResponse>> updateMenu(
		@Valid @RequestBody MenuUpdateRequest request
	) {
		CommonResponse<MenuResponse> response = menuService.updateMenu(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
