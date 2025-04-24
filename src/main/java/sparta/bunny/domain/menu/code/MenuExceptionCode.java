package sparta.bunny.domain.menu.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum MenuExceptionCode implements ResponseCode {
	// ❌ 400 BAD REQUEST
	CATEGORY_INVALID(false, HttpStatus.BAD_REQUEST, "잘못된 카테고리 ID입니다.", "CATEGORY_INVALID"),
	FIELD_MISSING(false, HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다.", "FIELD_MISSING"),
	// 🔒 401 UNAUTHORIZED
	UNAUTHORIZED(false, HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.", "UNAUTHORIZED"),
	// 🚫 403 FORBIDDEN
	NOT_BUSINESS_OWNER(false, HttpStatus.FORBIDDEN, "사장님 권한이 없습니다.", "NOT_BUSINESS_OWNER"),
	NOT_OWNER_OF_STORE(false, HttpStatus.FORBIDDEN, "본인의 가게가 아닙니다.", "NOT_OWNER_OF_STORE"),

	// 404 NOT_FOUND
	NOT_FOUND_STORE(false, HttpStatus.NOT_FOUND, "스토어가 없습니다.", "NOT_FOUND_STORE");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
