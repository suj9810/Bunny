package sparta.bunny.domain.order.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import sparta.bunny.common.exception.BaseException;
import sparta.bunny.common.response.ResponseCode;

@Getter
public class OrderException extends BaseException {

	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public OrderException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
