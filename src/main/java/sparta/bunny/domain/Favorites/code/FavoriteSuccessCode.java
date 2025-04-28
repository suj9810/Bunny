package sparta.bunny.domain.Favorites.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum FavoriteSuccessCode implements ResponseCode {

	FAVORITE_ADD_SUCCESS(true, HttpStatus.OK, "즐겨찾기 추가 성공", "FAVORITE_ADD_SUCCESS"),
	FAVORITE_REMOVE_SUCCESS(true, HttpStatus.OK, "즐겨찾기 삭제 성공", "FAVORITE_REMOVE_SUCCESS");

	private final boolean success;
	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}

