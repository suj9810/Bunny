package sparta.bunny.domain.menu.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import sparta.bunny.common.exception.BaseException;
import sparta.bunny.common.response.ResponseCode;

@Getter
public class MenuException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public MenuException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}