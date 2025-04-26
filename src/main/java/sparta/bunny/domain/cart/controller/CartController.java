package sparta.bunny.domain.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.cart.dto.CartMenuRequestDto;
import sparta.bunny.domain.cart.dto.CartMenuResponseDto;
import sparta.bunny.domain.cart.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
@PreAuthorize("hasRole('USER')")
public class CartController {

	private final CartService cartService;

	// 장바구니에 메뉴 추가
	// userId 가져오는건 나중에 변경
	@PostMapping("/{storeId}")
	public ResponseEntity<Void> addToCart(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long storeId,
		@RequestBody CartMenuRequestDto requestDto
	) {
		cartService.addToCart(userDetails, storeId, requestDto);
		return ResponseEntity.ok().build();
	}

	// 장바구니 조회
	@GetMapping
	public ResponseEntity<CartMenuResponseDto> getCart(@RequestParam Long userId) {
		return ResponseEntity.ok(cartService.getCart(userId));
	}

	// 메뉴 1개 삭제
	@DeleteMapping("/{menuId}")
	public ResponseEntity<Void> removeMenu(
		@RequestParam Long userId,
		@PathVariable Long menuId
	) {
		cartService.removeMenu(userId, menuId);
		return ResponseEntity.ok().build();
	}

	// 장바구니 전체 비우기
	@DeleteMapping("/clear")
	public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
		cartService.clearCart(userId);
		return ResponseEntity.ok().build();
	}
}

