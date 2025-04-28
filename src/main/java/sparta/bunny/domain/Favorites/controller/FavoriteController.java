package sparta.bunny.domain.Favorites.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.Favorites.code.FavoriteSuccessCode;
import sparta.bunny.domain.Favorites.service.FavoriteService;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;

@RestController
@RequestMapping("/favorites")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class FavoriteController {

	private final FavoriteService favoriteService;

	// 가게 즐겨찾기 추가
	@PostMapping("/add")
	public ResponseEntity<CommonResponse<Object>> addFavorite(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam Long storeId) {

		favoriteService.addFavorite(userDetails.getUser().getId(), storeId);

		return ResponseEntity.ok(CommonResponse.of(FavoriteSuccessCode.FAVORITE_ADD_SUCCESS, null));
	}

	// 가게 즐겨찾기 삭제
	@DeleteMapping("/remove")
	public ResponseEntity<CommonResponse<Object>> removeFavorite(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam Long storeId) {

		favoriteService.removeFavorite(userDetails.getUser().getId(), storeId);

		return ResponseEntity.ok(CommonResponse.of(FavoriteSuccessCode.FAVORITE_REMOVE_SUCCESS, null));
	}
}
