package sparta.bunny.domain.user.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ResponseCode {
	//400 BAD_REQUEST
	INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다.", "INVALID_PASSWORD_FORMAT"),
	PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.", "PASSWORD_MISMATCH"),
	SOCIAL_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "소셜 로그인 타입이 일치하지 않습니다.", "SOCIAL_TYPE_MISMATCH"),

	// 401 UNAUTHORIZED
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.", "LOGIN_FAILED"),

	// 403 FORBIDDEN
	UNAUTHORIZED_USER(HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없습니다.", "UNAUTHORIZED_USER"),

	// 404 NOT_FOUND
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.", "USER_NOT_FOUND"),

	// 409 CONFLICT
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다.", "DUPLICATE_EMAIL");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
