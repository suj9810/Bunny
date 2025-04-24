package sparta.bunny.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sparta.bunny.domain.cart.dto.CartDto;
import sparta.bunny.domain.cart.dto.CartMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, CartDto> redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:";

    /**
     * 장바구니 메뉴 추가
     * @param userId 유저
     * @param storeId 가게
     * @param item 메뉴
     */
    public void addToCart(Long userId, Long storeId, CartMenuItem item) {

        // cart:userId 형태로 key 저장
        String key = CART_KEY_PREFIX + userId;

        // Redis 에서 해당 유저의 장바구니(CartDto)를 꺼냄
        CartDto cart = redisTemplate.opsForValue().get(key);

        // 장바구니가 없거나 다른 가게 메뉴이면 새로 생성
        // 한 가게만 주문 가능하게 하기 위함
        if (cart == null || !storeId.equals(cart.getStoreId())) {
            // 새로운 장바구니 생성
            cart = new CartDto(storeId);
        }

        // 메뉴가 이미 있는지 확인
        Optional<CartMenuItem> existing = cart.getItems().stream()
                .filter(i -> i.getMenuId().equals(item.getMenuId()))
                .findFirst();

        if (existing.isPresent()) {
            // 수량 증가
            existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
        } else {
            cart.getItems().add(item);
        }

        // Redis 에 다시 저장
        redisTemplate.opsForValue().set(key, cart);
    }

    /**
     * 장바구니 조회
     * @param userId 유저
     * @return
     */
    public CartDto getCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        CartDto cart = redisTemplate.opsForValue().get(key);
        return cart != null ? cart : new CartDto(null, new ArrayList<>());
    }

    /**
     * 특정 메뉴 삭제
     * @param userId
     * @param menuId
     */
    public void removeMenu(Long userId, Long menuId) {
        String key = "cart:" + userId;
        CartDto cart = redisTemplate.opsForValue().get(key);

        if (cart == null) {
            return; // 장바구니가 없으면 그냥 아무것도 안 함
        }

        List<CartMenuItem> items = cart.getItems();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getMenuId().equals(menuId)) {
                items.remove(i); // 삭제
                break;
            }
        }

        redisTemplate.opsForValue().set(key, cart);
    }


    /**
     * 장바구니 전체 삭제
     * @param userId
     */
    public void clearCart(Long userId) {
        redisTemplate.delete(CART_KEY_PREFIX + userId);
    }
}
