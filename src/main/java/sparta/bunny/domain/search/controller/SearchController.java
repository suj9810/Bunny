package sparta.bunny.domain.search.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
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

    /**
     * 가게 검색 및 검색 내역 저장 (회원)
     * @param userDetails
     * @param dto
     * @return 키워드가 포함된 가게 정보 및 해당 가게의 메뉴 이름들 리스트로 반환
     */
    @PostMapping("/user")
    public ResponseEntity<List<SearchResponseDto>> searchForUsers(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestBody @Valid SearchRequestDto dto) {
        // 토큰이 존재할 때(회원)
        log.info("current user : {} ", userDetails.getUser().getEmail());
        log.info("search request: {}", dto);
        return ResponseEntity.ok(searchService.getAndSaveSearchLog(userDetails.getUser().getId(), dto.getKeyword()));
    }

    /**
     * 가게 검색 및 검색 내영 저장 (비회원)
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<List<SearchResponseDto>> search(@RequestBody @Valid SearchRequestDto dto) {

        // 토큰이 존재하지 않을 때(비회원)
        log.info("search request: {}", dto);
        return ResponseEntity.ok(searchService.getAndSaveSearchLog(null, dto.getKeyword()));
    }

    /**
     * 인기 검색어 top 10 조회
     *
     * @return
     */
    @GetMapping("/trending")
    public ResponseEntity<List<PopularKeywordDto>> getPopularKeywords() {

        return ResponseEntity.ok(searchService.getPopularKeywords());
    }

    /**
     * 내 검색기록 조회
     *
     * @param userDetails
     * @return
     */
    @GetMapping("/histories")
    public ResponseEntity<List<SearchHistoriesDto>> getMySearchHistories(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("current user : {} ", userDetails.getUser().getEmail());

        return ResponseEntity.ok(searchService.getHistories(userDetails.getUser().getId()));
    }

}
