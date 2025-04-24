package sparta.bunny.domain.user.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import sparta.bunny.common.exception.BaseException;
import sparta.bunny.common.response.ResponseCode;

@Getter
public class UserException extends BaseException {

	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public UserException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = getHttpStatus();
	}
}
