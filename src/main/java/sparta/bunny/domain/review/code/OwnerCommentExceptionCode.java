package sparta.bunny.domain.review.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum OwnerCommentExceptionCode implements ResponseCode {
	NOT_OWNER_OF_STORE(false, HttpStatus.FORBIDDEN, "가게 사장이 아닙니다.", "NOT_OWNER_OF_STORE");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
