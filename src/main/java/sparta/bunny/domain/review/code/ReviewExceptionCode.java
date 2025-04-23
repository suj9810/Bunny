package sparta.bunny.domain.review.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum ReviewExceptionCode implements ResponseCode {
	ALREADY_REVIEWED(false, HttpStatus.CONFLICT, "이미 리뷰가 등록되었습니다.", "ALREADY_REVIEWED"),
	DELIVERY_NOT_COMPLETE(false, HttpStatus.BAD_REQUEST, "아직 배달이 진행중입니다.", "DELIVERY_NOT_COMPLETE"),
	NOT_OWNER_OF_ORDER(false, HttpStatus.FORBIDDEN, "자신의 주문이 아닙니다.", "NOT_OWNER_OF_ORDER"),
	INVALID_REVIEW_RESPONSE(false, HttpStatus.BAD_REQUEST, "잘못된 리뷰 조회 요청입니다.", "INVALID_REVIEW_REQUEST"),
	INVALID_REVIEW_REQUEST(false, HttpStatus.BAD_REQUEST, "잘못된 리뷰 요청입니다.", "INVALID_REVIEW_REQUEST"),
	NOT_OWNER_OF_STORE(false, HttpStatus.FORBIDDEN, "가게 사장이 아닙니다.", "NOT_OWNER_OF_STORE"),
	REVIEW_NOT_FOUND(false, HttpStatus.NOT_FOUND, "리뷰 조회에 실패하였습니다.", "REVIEW_NOT_FOUND");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
