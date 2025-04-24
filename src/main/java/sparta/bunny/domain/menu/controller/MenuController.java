package sparta.bunny.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.menu.dto.request.MenuCreateRequest;
import sparta.bunny.domain.menu.dto.response.MenuCreateResponse;
import sparta.bunny.domain.menu.service.MenuService;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	@PostMapping
	public ResponseEntity<CommonResponse<MenuCreateResponse>> addMenu(
		@Valid @RequestBody MenuCreateRequest request) {
		CommonResponse<MenuCreateResponse> response = menuService.saveMenu(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
