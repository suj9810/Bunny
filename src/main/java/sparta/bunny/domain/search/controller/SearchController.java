package sparta.bunny.domain.search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sparta.bunny.domain.search.dto.response.PopularKeywordDto;
import sparta.bunny.domain.search.dto.response.SearchHistoriesDto;
import sparta.bunny.domain.search.dto.response.SearchResponseDto;

import java.util.List;

@RestController
public class SearchController {

    // 검색 및 검색 기록 저장
    @PostMapping("/searches")
    public ResponseEntity<List<SearchResponseDto>> search() {

        return null;
    }

    // 인기 Top 10 검색 기록 조회
    @GetMapping("/searches-trending")
    public ResponseEntity<List<PopularKeywordDto>> getPopularKeywords() {

        return null;
    }

    // 내 검색기록 조회
    @GetMapping("/searches-histories")
    public ResponseEntity<List<SearchHistoriesDto>> getMySearchHistories() {

        return null;
    }

}
