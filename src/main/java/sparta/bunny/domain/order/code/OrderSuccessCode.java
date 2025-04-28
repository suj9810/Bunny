package sparta.bunny.domain.order.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum OrderSuccessCode implements ResponseCode {

	ORDER_CREATE_SUCCESS(true, HttpStatus.CREATED, "주문 완료", "STORE_CREATE_SUCCESS"),
	ORDER_CANCEL_SUCCESS(true, HttpStatus.OK, "주문 취소 완료", "ORDER_CANCEL_SUCCESS"),
	ORDER_GET_SUCCESS(true, HttpStatus.OK, "주문 조회 완료", "ORDER_GET_SUCCESS"),
	ORDER_LIST_GET_SUCCESS(true, HttpStatus.OK, "주문 목록 조회 완료", "ORDER_LIST_GET_SUCCESS"),
	ORDER_STATUS_UPDATE_SUCCESS(true, HttpStatus.OK, "주문 상태 변경 완료", "ORDER_STATUS_UPDATE_SUCCESS");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
