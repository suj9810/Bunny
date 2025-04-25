package sparta.bunny.domain.review.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import sparta.bunny.common.exception.BaseException;
import sparta.bunny.common.response.ResponseCode;

@Getter
public class OwnerCommentException extends BaseException {
	private final ResponseCode responseCode;
	private final HttpStatus httpStatus;

	public OwnerCommentException(ResponseCode responseCode) {
		this.responseCode = responseCode;
		this.httpStatus = responseCode.getHttpStatus();
	}
}
