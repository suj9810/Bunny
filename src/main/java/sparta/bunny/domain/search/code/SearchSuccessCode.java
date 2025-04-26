package sparta.bunny.domain.search.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sparta.bunny.common.response.ResponseCode;

@Getter
@RequiredArgsConstructor
public enum SearchSuccessCode implements ResponseCode {
    SEARCH_AND_HISTORY_SAVE_SUCCESS(true, HttpStatus.OK, "검색 성공", "SEARCH_SUCCESS"),
    SEARCH_HISTORIES_FETCH_SUCCESS(true, HttpStatus.OK, "검색 기록 조회 성공", "SEARCH_HISTORIES_FETCH_SUCCESS"),
    SEARCH_HISTORIES_DELETE_SUCCESS(true, HttpStatus.OK, "검색 기록 삭제 성공", "SEARCH_HISTORIES_DELETE_SUCCESS"),
    POPULAR_KEYWORDS_FETCH_SUCCESS(true, HttpStatus.OK, "인기 검색어 조회 성공", "POPULAR_KEYWORDS_FETCH_SUCCESS");

    private final boolean success;
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
