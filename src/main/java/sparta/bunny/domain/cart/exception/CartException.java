package sparta.bunny.domain.cart.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import sparta.bunny.common.exception.BaseException;
import sparta.bunny.common.response.ResponseCode;

@Getter
public class CartException extends BaseException {

	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public CartException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
