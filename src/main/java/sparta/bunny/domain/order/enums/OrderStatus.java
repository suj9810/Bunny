package sparta.bunny.domain.order.enums;

import java.util.Arrays;

public enum OrderStatus {
	PENDING,        // 주문 접수 중
	CANCELED,       // 취소
	ACCEPTED,       // 수락
	DELIVERING,     // 배달중
	DELIVERED;       // 배달완료

	public static OrderStatus of(String orderStatus) {
		return Arrays.stream(OrderStatus.values())
			.filter(s -> s.name().equalsIgnoreCase(orderStatus))
			.findFirst()
			.orElseThrow(() -> new NullPointerException("유효하지 않은 status"));
	}
}
