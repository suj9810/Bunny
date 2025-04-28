package sparta.bunny.domain.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.cart.dto.CartMenuRequestDto;
import sparta.bunny.domain.cart.dto.CartMenuResponseDto;
import sparta.bunny.domain.cart.entity.Cart;
import sparta.bunny.domain.cart.entity.CartMenu;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@InjectMocks
	private CartService cartService;

	@Mock
	private RedisTemplate<String, Cart> redisTemplate;

	@Mock
	private MenuRepository menuRepository;

	@Test
	@DisplayName("장바구니에 메뉴 추가 - 신규 추가")
	void addToCart_NewMenu_Success() {
		// given
		Long userId = 1L;
		Long storeId = 1L;
		Long menuId = 1L;
		CartMenuRequestDto requestDto = new CartMenuRequestDto(menuId, 2);

		Menu menu = Menu.builder()
			.id(menuId)
			.name("치킨")
			.price(15000)
			.build();

		User user = User.builder()
			.id(userId)
			.email("test@example.com")
			.password("encodedPassword")
			.userRole(UserRole.USER)
			.build();
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		
		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
		given(redisTemplate.opsForValue().get("cart:" + userId)).willReturn(null);

		// when
		cartService.addToCart(userDetails, storeId, requestDto);

		// then
		verify(redisTemplate, times(1)).opsForValue().set(eq("cart:" + userId), any(Cart.class));
	}

	@Test
	@DisplayName("장바구니 조회 - 존재할 때")
	void getCart_WhenExist_Success() {
		// given
		Long userId = 1L;
		Cart cart = new Cart(1L);
		cart.getMenus().add(new CartMenu(100L, "치킨", 15000, 2));
		given(redisTemplate.opsForValue().get("cart:" + userId)).willReturn(cart);

		// when
		CartMenuResponseDto response = cartService.getCart(userId);

		// then
		assertNotNull(response);
		assertEquals(1, response.getMenus().size());
		assertEquals("치킨", response.getMenus().get(0).getMenuName());
	}

	@Test
	@DisplayName("장바구니 삭제 - 메뉴 하나 삭제")
	void removeMenu_Success() {
		// given
		Long userId = 1L;
		Long menuId = 100L;

		Cart cart = new Cart(1L);
		cart.getMenus().add(new CartMenu(menuId, "치킨", 15000, 2));
		given(redisTemplate.opsForValue().get("cart:" + userId)).willReturn(cart);

		// when
		cartService.removeMenu(userId, menuId);

		// then
		verify(redisTemplate, times(1)).opsForValue().set(eq("cart:" + userId), any(Cart.class));
	}

	@Test
	@DisplayName("장바구니 전체 삭제")
	void clearCart_Success() {
		// given
		Long userId = 1L;

		// when
		cartService.clearCart(userId);

		// then
		verify(redisTemplate, times(1)).delete("cart:" + userId);
	}
}
