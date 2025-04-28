package sparta.bunny.domain.Favorites.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum FavoriteExceptionCode implements ResponseCode {

	FAVORITE_ALREADY_EXISTS(false, HttpStatus.BAD_REQUEST, "이미 즐겨찾기 추가된 가게입니다.", "FAVORITE_ALREADY_EXISTS"),
	STORE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "존재하지 않는 가게입니다.", "STORE_NOT_FOUND"),
	FAVORITE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "즐겨찾기에서 가게를 찾을 수 없습니다.", "FAVORITE_NOT_FOUND");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
