package sparta.bunny.domain.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import sparta.bunny.domain.cart.dto.CartMenuResponseDto;
import sparta.bunny.domain.cart.entity.CartMenu;
import sparta.bunny.domain.cart.service.CartService;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.enums.Status;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.order.dto.ChangeOrderStatusRequestDto;
import sparta.bunny.domain.order.dto.OrderResponseDto;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.order.repository.OrderRepository;
import sparta.bunny.domain.stores.entity.Category;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;
import sparta.bunny.domain.user.repository.UserRepository;

class OrderServiceTest {

	@Mock
	private CartService cartService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderService orderService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createOrder_success() {
		// given
		Long userId = 1L;
		Long storeId = 1L;
		Long menuId = 1L;

		User user = User.builder()
			.email("test@example.com")
			.password("password")
			.nickname("tester")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(false)
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(user, "orderList", new ArrayList<>());

		Store store = Store.builder()
			.user(user)
			.storeName("Test Store")
			.openTime("09:00")
			.closeTime("22:00")
			.minOrderPrice(5000)
			.notice("Notice")
			.isClosed(false)
			.categoryName(Category.한식)
			.build();
		ReflectionTestUtils.setField(store, "id", storeId);

		Menu menu = Menu.builder()
			.store(store)
			.name("치킨")
			.description("맛있는 치킨")
			.price(10000)
			.status(Status.ACTIVE)
			.build();
		ReflectionTestUtils.setField(menu, "id", menuId);

		CartMenu cartMenu = new CartMenu(menuId, "치킨", 10000, 1);
		CartMenuResponseDto cartDto = new CartMenuResponseDto(storeId, List.of(cartMenu));

		given(cartService.getCart(userId)).willReturn(cartDto);
		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
		given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
		given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		OrderResponseDto response = orderService.createOrder(userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getItems()).hasSize(1);
		assertThat(response.getItems().get(0).getMenuName()).isEqualTo("치킨");

		then(cartService).should().clearCart(userId);
	}

	@Test
	void getOrder_success() {
		// given
		Long userId = 1L;
		Long orderId = 1L;

		User user = User.builder()
			.email("test2@example.com")
			.password("password2")
			.nickname("tester2")
			.userRole(UserRole.USER)
			.userNumber("010-2222-3333")
			.isDeleted(false)
			.build();
		ReflectionTestUtils.setField(user, "id", userId);

		Store store = Store.builder()
			.user(user)
			.storeName("Another Store")
			.openTime("08:00")
			.closeTime("23:00")
			.minOrderPrice(5000)
			.notice("Notice 2")
			.isClosed(false)
			.categoryName(Category.양식)
			.build();

		Order order = new Order(user, store);
		ReflectionTestUtils.setField(order, "id", orderId);

		given(orderRepository.findByIdWithOrderMenus(orderId)).willReturn(Optional.of(order));

		// when
		OrderResponseDto response = orderService.getOrder(userId, orderId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getOrderId()).isEqualTo(orderId);
	}

	@Test
	void changeOrderStatus_success() {
		// given
		Long orderId = 1L;
		ChangeOrderStatusRequestDto requestDto = new ChangeOrderStatusRequestDto("ACCEPTED");

		User user = User.builder()
			.email("user3@example.com")
			.password("pass3")
			.nickname("tester3")
			.userRole(UserRole.USER)
			.userNumber("010-1111-2222")
			.isDeleted(false)
			.build();

		Store store = Store.builder()
			.user(user)
			.storeName("Third Store")
			.openTime("07:00")
			.closeTime("21:00")
			.minOrderPrice(4000)
			.notice("Third notice")
			.isClosed(false)
			.categoryName(Category.일식)
			.build();

		Order order = new Order(user, store);
		ReflectionTestUtils.setField(order, "id", orderId);

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		// when
		OrderResponseDto response = orderService.changeOrderStatus(orderId, requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ACCEPTED);
	}
}