package sparta.bunny.domain.menu.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.menu.dto.request.MenuCreateRequest;
import sparta.bunny.domain.menu.dto.request.MenuUpdateRequest;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.menu.service.MenuService;

/**
 * Menu Controller
 */
@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class MenuController {

	private final MenuService menuService;

	/**
	 * 메뉴 생성
	 *
	 * @param request
	 * @param userDetails the user details
	 * @return the response entity
	 * @throws IOException the io exception
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<MenuResponse>> createMenuWithImg(
		@ModelAttribute @Valid MenuCreateRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		CommonResponse<MenuResponse> response = menuService.saveMenu(request, userDetails.getUser().getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 메뉴 수정
	 *
	 * @param menuId the menu id
	 * @param request the request
	 * @param userDetails the user details
	 * @return the response entity
	 * @throws IOException the io exception
	 */
	@PutMapping("/{menuId}")
	public ResponseEntity<CommonResponse<MenuResponse>> updateMenu(
		@PathVariable Long menuId,
		@ModelAttribute @Valid MenuUpdateRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
		CommonResponse<MenuResponse> response = menuService.updateMenu(menuId, userDetails, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 메뉴 삭제 (softDelete)
	 *
	 * @param menuId the menu id
	 * @param userDetails the user details
	 * @return the response entity
	 */
	@DeleteMapping("/{menuId}")
	public ResponseEntity<CommonResponse<MenuResponse>> deleteMenu(
		@PathVariable Long menuId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		CommonResponse<MenuResponse> response = menuService.deleteMenu(menuId, userDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
	}
}
