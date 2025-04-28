package sparta.bunny.domain.order.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.cart.dto.CartMenuResponseDto;
import sparta.bunny.domain.cart.entity.CartMenu;
import sparta.bunny.domain.cart.service.CartService;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.order.dto.ChangeOrderStatusRequestDto;
import sparta.bunny.domain.order.dto.OrderMenuDto;
import sparta.bunny.domain.order.dto.OrderResponseDto;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.order.entity.OrderMenu;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.order.repository.OrderRepository;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;
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
			throw new IllegalArgumentException("장바구니가 비어 있습니다.");
		}

		//User 정보 가져오기
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		//가게 정보 가져오기
		Store store = storeRepository.findById(cart.getStoreId())
			.orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

		LocalTime now = LocalTime.now();

		//가게 오픈시간 전이거나 마감시간 지났을때
		if (now.isBefore(store.getOpenTime()) || now.isAfter(store.getCloseTime())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "주문 가능 시간이 아닙니다.");
		}

		//유저와 가게 정보로 주문 객체 만들기
		Order order = new Order(user, store);

		List<OrderMenuDto> itemDtos = new ArrayList<>();
		int totalPrice = 0;

		for (CartMenu cartItem : cart.getMenus()) {
			Menu menu = menuRepository.findById(cartItem.getMenuId())
				.orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));

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
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "최소 주문금액을 충족하지 못했습니다.");
		}

		orderRepository.save(order);

		cartService.clearCart(userId);

		return OrderResponseDto.of(order, itemDtos);
	}

	@Transactional(readOnly = true)
	public List<OrderResponseDto> getOrderList(Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 유저 아이디 입니다."));

		return user.getOrderList().stream()
			.map(OrderResponseDto::fromOrder)
			.toList();
	}

	@Transactional(readOnly = true)
	public OrderResponseDto getOrder(Long userId, Long orderId) {

		Order order = orderRepository.findByIdWithOrderMenus(orderId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 주문 입니다."));

		if (!userId.equals(order.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 접근입니다.");
		}

		return OrderResponseDto.fromOrder(order);
	}

	public void deleteOrder(Long userId, Long orderId) {

		Order order = orderRepository.findByIdWithOrderMenus(orderId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 주문 입니다."));

		if (!userId.equals(order.getUser().getId())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 접근입니다.");
		}

		if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "주문이 확정 되어 주문을 취소할 수 없습니다.");
		}

		orderRepository.delete(order);
	}

	@Transactional
	public OrderResponseDto changeOrderStatus(Long orderId, ChangeOrderStatusRequestDto requestDto) {

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 주문 입니다."));
		order.updateOrderStatus(OrderStatus.of(requestDto.getOrderStatus()));

		return OrderResponseDto.fromOrder(order);
	}
}
