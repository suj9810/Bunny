package sparta.bunny.domain.cart.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import sparta.bunny.domain.cart.entity.Cart;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private RedisTemplate<String, Cart> redisTemplate;

	@Mock
	private ValueOperations<String, Cart> valueOperations;

	@InjectMocks
	private CartService cartService;

	@Test
	void addToCart() {
		//given

		//when

		//then
	}

	@Test
	void getCart() {
	}

	@Test
	void removeMenu() {
	}

	@Test
	void clearCart() {
	}
}