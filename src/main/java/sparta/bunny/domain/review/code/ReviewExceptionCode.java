package sparta.bunny.domain.review.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum ReviewExceptionCode implements ResponseCode {
	ALREADY_REVIEWED(false, HttpStatus.CONFLICT, "이미 리뷰가 등록되었습니다.", "ALREADY_REVIEWED");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
