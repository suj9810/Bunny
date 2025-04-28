package sparta.bunny.domain.cart.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum CartExceptionCode implements ResponseCode {

	// UNAUTHORIZED
	CART_EMPTY(false, HttpStatus.UNAUTHORIZED, "장바구니가 비어있습니다.", "CART_EMPTY");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
