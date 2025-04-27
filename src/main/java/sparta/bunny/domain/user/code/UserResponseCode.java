package sparta.bunny.domain.user.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum UserResponseCode implements ResponseCode {
	// 201 created
	SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 완료", "SIGNUP_SUCCESS"),

	// 200 ok
	LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공", "LOGIN_SUCCESS"),
	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공", "LOGOUT_SUCCESS"),
	UPDATE_USER_SUCCESS(HttpStatus.OK, "회원 정보 수정 완료", "UPDATE_USER_SUCCESS"),
	FIND_USER_SUCCESS(HttpStatus.OK, "회원 조회 성공", "FIND_USER_SUCCESS"),
	DELETE_USER_SUCCESS(HttpStatus.OK, "회원 탈퇴 완료", "DELETE_USER_SUCCESS"),
	UPDATE_PASSWORD_SUCCESS(HttpStatus.OK, "비밀번호 수정 완료", "UPDATE_PASSWORD_SUCCESS");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;

}
