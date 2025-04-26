package sparta.bunny.domain.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import sparta.bunny.domain.order.entity.Order;

@Getter
public class OrderResponseDto {

	private final Long orderId;
	private final Long userId;
	private final Long storeId;
	private final LocalDateTime orderedAt;
	private final String orderStatus;
	private final List<OrderMenuDto> items;

	private OrderResponseDto(Long orderId, Long userId, Long storeId, LocalDateTime orderedAt, String orderStatus,
		List<OrderMenuDto> items) {
		this.orderId = orderId;
		this.userId = userId;
		this.storeId = storeId;
		this.orderedAt = orderedAt;
		this.orderStatus = orderStatus;
		this.items = items;
	}

	public static OrderResponseDto of(Order order, List<OrderMenuDto> items) {
		return new OrderResponseDto(order.getId(), order.getUser().getId(), order.getStore().getId(),
			order.getOrderedAt(), order.getOrderStatus().name(), items);
	}

	public static OrderResponseDto fromOrder(Order order) {
		return new OrderResponseDto(order.getId(), order.getUser().getId(), order.getStore().getId(),
			order.getOrderedAt(), order.getOrderStatus().name(), null);
	}
}