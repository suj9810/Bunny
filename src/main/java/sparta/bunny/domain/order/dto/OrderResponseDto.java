package sparta.bunny.domain.order.dto;

import lombok.Getter;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponseDto {

    private final Long orderId;
    private final Long userId;
    private final Long storeId;
    private final LocalDateTime orderedAt;
    private final String orderStatus;
    private final List<OrderMenuDto> items;

    public OrderResponseDto(Long orderId, Long userId, Long storeId, LocalDateTime orderedAt, String orderStatus, List<OrderMenuDto> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.storeId = storeId;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
        this.items = items;
    }
}