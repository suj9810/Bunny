package sparta.bunny.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.bunny.domain.cart.dto.CartDto;
import sparta.bunny.domain.cart.dto.CartMenuItem;
import sparta.bunny.domain.cart.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    // 장바구니에 메뉴 추가
    // userId 가져오는건 나중에 변경
    @PostMapping
    public ResponseEntity<Void> addToCart(
            @RequestParam Long userId,
            @RequestParam Long storeId,
            @RequestBody CartMenuItem item
    ) {
        cartService.addToCart(userId, storeId, item);
        return ResponseEntity.ok().build();
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<CartDto> getCart(@RequestParam Long userId) {
        CartDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
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

