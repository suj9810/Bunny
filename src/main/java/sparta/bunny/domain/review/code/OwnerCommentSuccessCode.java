package sparta.bunny.domain.review.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum OwnerCommentSuccessCode implements ResponseCode {
	REVIEW_COMMENT_CREATE_SUCCESS(true, HttpStatus.CREATED, "리뷰 응답 생성 성공", "REVIEW_COMMENT_CREATE_SUCCESS");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
