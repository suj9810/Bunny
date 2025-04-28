package sparta.bunny.domain.search.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.search.code.SearchSuccessCode;
import sparta.bunny.domain.search.dto.request.SearchRequestDto;
import sparta.bunny.domain.search.dto.response.PopularKeywordDto;
import sparta.bunny.domain.search.dto.response.SearchHistoriesDto;
import sparta.bunny.domain.search.dto.response.SearchResponseDto;
import sparta.bunny.domain.search.service.SearchService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/searches")
public class SearchController {

    private final SearchService searchService;
    // TODO : 예외처리 하기 (BASEEXCEPION, CommonResponse)
    /**
     * 가게 검색 및 검색 내역 저장 (회원)
     * @param userDetails
     * @param dto
     * @return 키워드가 포함된 가게 정보 및 해당 가게의 메뉴 이름들 리스트로 반환
     */

    @PostMapping
    public ResponseEntity<CommonResponse<List<SearchResponseDto>>> searchForUsers(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                   @RequestBody @Valid SearchRequestDto dto) {
        List<SearchResponseDto> result = searchService.getAndSaveSearchLog(
                userDetails == null ? null : userDetails.getUser().getId(), dto.getKeyword());

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.of(SearchSuccessCode.SEARCH_AND_HISTORY_SAVE_SUCCESS, result));
    }

    /**
     * 인기 검색어 top 10 조회
     *
     * @return
     */
    @GetMapping("/trending")
    public ResponseEntity<CommonResponse<List<PopularKeywordDto>>> getPopularKeywords() {

        List<PopularKeywordDto> result = searchService.getPopularKeywords();
      
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.of(SearchSuccessCode.POPULAR_KEYWORDS_FETCH_SUCCESS, result));
    }

    /**
     * 내 검색기록 조회
     *
     * @param userDetails
     * @return
     */
    @GetMapping("/histories")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommonResponse<List<SearchHistoriesDto>>> getMySearchHistories(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<SearchHistoriesDto> result = searchService.getHistories(userDetails.getUser().getId());

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.of(SearchSuccessCode.SEARCH_HISTORIES_FETCH_SUCCESS, result));
    }

    /**
     * 내 검색기록 삭제
     * @param userDetails
     * @return
     */
    @DeleteMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommonResponse> deleteSearchLogs(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        searchService.deleteHistories(userDetails.getUser());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonResponse.of(SearchSuccessCode.SEARCH_HISTORIES_DELETE_SUCCESS, null));
    }

}
