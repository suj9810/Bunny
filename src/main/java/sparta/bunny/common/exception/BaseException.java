package sparta.bunny.common.exception;

import org.springframework.http.HttpStatus;

import sparta.bunny.common.response.ResponseCode;

public abstract class BaseException extends RuntimeException {
	public abstract ResponseCode getResponseCode();

	public abstract HttpStatus getHttpStatus();
}
