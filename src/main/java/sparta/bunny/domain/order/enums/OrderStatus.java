package sparta.bunny.domain.order.enums;

import java.util.Arrays;

public enum OrderStatus {
	WAITING,        // 대기
	CANCELED,       // 취소
	ACCEPTED,       // 수락
	PICKED_UP,      // 픽업완료
	DELIVERING,     // 배달중
	DELIVERED;       // 배달완료

	public static OrderStatus of(String status) {
		return Arrays.stream(OrderStatus.values())
			.filter(s -> s.name().equalsIgnoreCase(status))
			.findFirst()
			.orElseThrow(() -> new NullPointerException("유효하지 않은 status"));
	}
}
