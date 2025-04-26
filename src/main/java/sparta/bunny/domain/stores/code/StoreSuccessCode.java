package sparta.bunny.domain.stores.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum StoreSuccessCode implements ResponseCode {

	STORE_CREATE_SUCCESS(true, HttpStatus.CREATED, "가게 생성 완료", "STORE_CREATE_SUCCESS"),
	STORE_UPDATE_SUCCESS(true, HttpStatus.OK, "가게 수정 완료", "STORE_UPDATE_SUCCESS"),
	STORE_CLOSE_SUCCESS(true, HttpStatus.OK, "가게 폐업 처리 완료", "STORE_CLOSE_SUCCESS"),
	STORE_REOPEN_SUCCESS(true, HttpStatus.OK, "가게 재오픈 완료", "STORE_REOPEN_SUCCESS"),
	STORE_FETCH_SUCCESS(true, HttpStatus.OK, "가게 조회 완료", "STORE_FETCH_SUCCESS"),
	STORE_FETCH_ALL_SUCCESS(true, HttpStatus.OK, "전체 가게 조회 완료", "STORE_FETCH_ALL_SUCCESS"),
	STORE_FAVORITE_SUCCESS(true, HttpStatus.OK, "즐겨찾기 완료", "STORE_FAVORITE_SUCCESS"),
	STORE_CLOSE_CANCELLATION_SUCCESS(true, HttpStatus.OK, "가게 폐업 해제 완료",
		"STORE_CLOSE_CANCELLATION_SUCCESS"); // 폐업 해제 추가

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
