package sparta.bunny.domain.cart.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum CartSuccessCode implements ResponseCode {

	CART_ADD_SUCCESS(true, HttpStatus.CREATED, "장바구니에 메뉴 추가 완료", "CART_ADD_SUCCESS"),
	CART_GET_SUCCESS(true, HttpStatus.OK, "장바구니 조회 완료", "CART_GET_SUCCESS"),
	CART_MENU_DELETE_SUCCESS(true, HttpStatus.OK, "장바구니 메뉴 삭제 완료", "CART_MENU_DELETE_SUCCESS"),
	CART_CLEAR_SUCCESS(true, HttpStatus.OK, "장바구니 비우기 완료", "CART_CLEAR_SUCCESS");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
