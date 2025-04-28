package sparta.bunny.domain.order.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum OrderExceptionCode implements ResponseCode {

	//BAD_REQUEST
	ORDER_NOT_AVAILABLE_TIME(false, HttpStatus.BAD_REQUEST, "주문 가능한 시간이 아닙니다.", "ORDER_NOT_AVAILABLE_TIME"),
	ORDER_MIN_PRICE_NOT_MET(false, HttpStatus.BAD_REQUEST, "최소 주문 금액을 확인해주세요.", "ORDER_MIN_PRICE_NOT_MET"),
	ORDER_CANNOT_CANCEL_CONFIRMED(false, HttpStatus.BAD_REQUEST, "주문이 확정되어 취소 할 수 없습니다.", "ORDER_MIN_PRICE_NOT_MET"),

	//UNAUTHORIZED
	UNAUTHORIZED_ACCESS(false, HttpStatus.UNAUTHORIZED, "잘못된 접근입니다.", "UNAUTHORIZED_ACCESS"),

	//NOT_FOUND
	ORDER_NOT_FOUND(false, HttpStatus.NOT_FOUND, "유효하지 않은 주문입니다.", "STORE_LIMIT_EXCEEDED"); // 추가된 code 값

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
