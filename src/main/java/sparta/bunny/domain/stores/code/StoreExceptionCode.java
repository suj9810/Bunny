package sparta.bunny.domain.stores.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum StoreExceptionCode implements ResponseCode {

	STORE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "존재하지 않는 가게입니다.", "STORE_NOT_FOUND"),
	UNAUTHORIZED_ACCESS(false, HttpStatus.FORBIDDEN, "권한이 없습니다.", "UNAUTHORIZED_ACCESS");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
