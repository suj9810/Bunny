package sparta.bunny.domain.cart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.cart.dto.CartMenuRequestDto;
import sparta.bunny.domain.cart.dto.CartMenuResponseDto;
import sparta.bunny.domain.cart.entity.Cart;
import sparta.bunny.domain.cart.entity.CartMenu;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;

@Service
@RequiredArgsConstructor
public class CartService {

	private final RedisTemplate<String, Cart> redisTemplate;

	private static final String CART_KEY_PREFIX = "cart:";

	private final MenuRepository menuRepository;

	/**
	 * 장바구니 메뉴 추가
	 * @param userId 유저
	 * @param storeId 가게
	 * @param requestDto 요청 데이터
	 */
	public void addToCart(Long userId, Long storeId, CartMenuRequestDto requestDto) {

		// cart:userId 형태로 key 저장
		String key = CART_KEY_PREFIX + userId;

		// Redis 에서 해당 유저의 장바구니(Cart)를 꺼냄
		Cart cart = redisTemplate.opsForValue().get(key);

		// 장바구니가 없거나 다른 가게 메뉴이면 새로 생성
		// 한 가게만 주문 가능하게 하기 위함
		if (cart == null || !storeId.equals(cart.getStoreId())) {
			// 새로운 장바구니 생성
			cart = new Cart(storeId);
		}

		// 메뉴가 이미 있는지 확인
		Optional<CartMenu> existing = cart.getMenus().stream()
			.filter(i -> i.getMenuId().equals(requestDto.getMenuId()))
			.findFirst();
		if (existing.isPresent()) {
			// 수량 증가
			existing.get().setQuantity(existing.get().getQuantity() + requestDto.getQuantity());
		} else {
			Menu menu = menuRepository.findById(requestDto.getMenuId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 메뉴 입니다."));
			CartMenu cartMenu = new CartMenu(menu.getId(), menu.getName(), menu.getPrice(), requestDto.getQuantity());
			cart.getMenus().add(cartMenu);
		}

		// Redis 에 다시 저장
		redisTemplate.opsForValue().set(key, cart);
	}

	/**
	 * 장바구니 조회
	 * @param userId 유저
	 * @return
	 */
	public CartMenuResponseDto getCart(Long userId) {
		String key = CART_KEY_PREFIX + userId;
		Cart cart = redisTemplate.opsForValue().get(key);

		if (cart == null) {
			return new CartMenuResponseDto(null, new ArrayList<>());
		}

		List<CartMenu> menuItems = cart.getMenus().stream()
			.map(menu -> new CartMenu(
				menu.getMenuId(),
				menu.getMenuName(),
				menu.getMenuPrice(),
				menu.getQuantity()
			))
			.toList();

		return new CartMenuResponseDto(cart.getStoreId(), menuItems);
	}

	/**
	 * 특정 메뉴 삭제
	 * @param userId
	 * @param menuId
	 */
	public void removeMenu(Long userId, Long menuId) {
		String key = "cart:" + userId;
		Cart cart = redisTemplate.opsForValue().get(key);

		if (cart == null) {
			return; // 장바구니가 없으면 그냥 아무것도 안 함
		}

		List<CartMenu> items = cart.getMenus();

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
