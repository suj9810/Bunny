package sparta.bunny.domain.menu.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum MenuSuccessCode implements ResponseCode {
	// ✅ 201 CREATE
	MENU_CREATE_SUCCESS(true, HttpStatus.CREATED, "메뉴 생성 성공", "MENU_CREATE_SUCCESS"),
	// MENU_FOUND_SUCCESS(true, HttpStatus.OK, "리뷰 조회 성공", "REVIEW_FOUND_SUCCESS"),
	// ✅ 200 OK
	MENU_SUCCESS(true, HttpStatus.CREATED, "메뉴 응답 성공", "MENU_SUCCESS");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
