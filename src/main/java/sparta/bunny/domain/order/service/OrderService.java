package sparta.bunny.domain.order.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.cart.code.CartExceptionCode;
import sparta.bunny.domain.cart.dto.CartMenuResponseDto;
import sparta.bunny.domain.cart.entity.CartMenu;
import sparta.bunny.domain.cart.exception.CartException;
import sparta.bunny.domain.cart.service.CartService;
import sparta.bunny.domain.menu.code.MenuExceptionCode;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.exception.MenuException;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.order.code.OrderExceptionCode;
import sparta.bunny.domain.order.dto.ChangeOrderStatusRequestDto;
import sparta.bunny.domain.order.dto.OrderMenuDto;
import sparta.bunny.domain.order.dto.OrderResponseDto;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.order.entity.OrderMenu;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.order.exception.OrderException;
import sparta.bunny.domain.order.repository.OrderRepository;
import sparta.bunny.domain.stores.code.StoreExceptionCode;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.exception.StoreException;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.code.UserErrorCode;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.exception.UserException;
import sparta.bunny.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CartService cartService;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final OrderRepository orderRepository;

	@SuppressWarnings({"checkstyle:WhitespaceAround", "checkstyle:RegexpSingleline"})
	@Transactional
	public OrderResponseDto createOrder(Long userId) {

		//cart 정보 가져오기
		CartMenuResponseDto cart = cartService.getCart(userId);

		if (cart.getMenus() == null || cart.getMenus().isEmpty()) {
			throw new CartException(CartExceptionCode.CART_EMPTY);
		}

		//User 정보 가져오기
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		//가게 정보 가져오기
		Store store = storeRepository.findById(cart.getStoreId())
			.orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));

		LocalTime now = LocalTime.now();

		//가게 오픈시간 전이거나 마감시간 지났을때
		if (now.isBefore(store.getOpenTime()) || now.isAfter(store.getCloseTime())) {
			throw new OrderException(OrderExceptionCode.ORDER_NOT_AVAILABLE_TIME);
		}

		//유저와 가게 정보로 주문 객체 만들기
		Order order = new Order(user, store);

		List<OrderMenuDto> itemDtos = new ArrayList<>();
		int totalPrice = 0;

		for (CartMenu cartItem : cart.getMenus()) {
			Menu menu = menuRepository.findById(cartItem.getMenuId())
				.orElseThrow(() -> new MenuException(MenuExceptionCode.NOT_FOUND_MENU));

			totalPrice += menu.getPrice() * cartItem.getQuantity();

			OrderMenu orderMenu = new OrderMenu(menu, cartItem.getQuantity());
			order.addMenu(orderMenu); // 양방향 연결
			user.addOrder(order);

			itemDtos.add(new OrderMenuDto(
				menu.getId(),
				menu.getName(),
				cartItem.getQuantity(),
				menu.getPrice()
			));
		}

		// 최소 주문금액 미달일때
		if (totalPrice < store.getMinOrderPrice()) {
			throw new OrderException(OrderExceptionCode.ORDER_MIN_PRICE_NOT_MET);
		}

		orderRepository.save(order);

		cartService.clearCart(userId);

		return OrderResponseDto.of(order, itemDtos);
	}

	@Transactional(readOnly = true)
	public List<OrderResponseDto> getOrderList(Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		return user.getOrderList().stream()
			.map(OrderResponseDto::fromOrder)
			.toList();
	}

	@Transactional(readOnly = true)
	public OrderResponseDto getOrder(Long userId, Long orderId) {

		Order order = orderRepository.findByIdWithOrderMenus(orderId)
			.orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));

		if (!userId.equals(order.getUser().getId())) {
			throw new OrderException(OrderExceptionCode.UNAUTHORIZED_ACCESS);
		}

		return OrderResponseDto.fromOrder(order);
	}

	public void deleteOrder(Long userId, Long orderId) {

		Order order = orderRepository.findByIdWithOrderMenus(orderId)
			.orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));

		if (!userId.equals(order.getUser().getId())) {
			throw new OrderException(OrderExceptionCode.UNAUTHORIZED_ACCESS);
		}

		if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
			throw new OrderException(OrderExceptionCode.ORDER_CANNOT_CANCEL_CONFIRMED);
		}

		orderRepository.delete(order);
	}

	@Transactional
	public OrderResponseDto changeOrderStatus(Long orderId, ChangeOrderStatusRequestDto requestDto) {

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));
		order.updateOrderStatus(OrderStatus.of(requestDto.getOrderStatus()));

		return OrderResponseDto.fromOrder(order);
	}
}
