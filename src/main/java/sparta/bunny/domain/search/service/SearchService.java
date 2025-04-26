package sparta.bunny.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sparta.bunny.domain.search.dto.response.MenuSummaryResponseDto;
import sparta.bunny.domain.search.dto.response.PopularKeywordDto;
import sparta.bunny.domain.search.dto.response.SearchHistoriesDto;
import sparta.bunny.domain.search.dto.response.SearchResponseDto;
import sparta.bunny.domain.search.entity.SearchLog;
import sparta.bunny.domain.search.repository.SearchLogRepository;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.code.UserErrorCode;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.exception.UserException;
import sparta.bunny.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchLogRepository searchLogRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 검색 결과 조회 및 기록 저장
    public List<SearchResponseDto> getAndSaveSearchLog(Long userId, String keyword) {

        // 회원
        if (userId != null) {
            User currentUser = userRepository.findById(userId).orElse(null);
            SearchLog searchLog = searchLogRepository.save(new SearchLog(currentUser, keyword));
        }
        // 비회원
        SearchLog searchLog = searchLogRepository.save(new SearchLog(null, keyword));


        // 가게 정보 및 해당 가게의 메뉴 이름 간단 표시
        return storeRepository.findByStoreNameContaining(keyword).stream()
                .map(store -> new SearchResponseDto(
                        store.getId(),
                        store.getStoreName(),
                        store.getMinOrderPrice(),
                        store.getOpenTime(),
                        store.getCloseTime(),
                        store.getIsClosed(),
                        store.getMenus().stream().map(menu -> new MenuSummaryResponseDto(
                                menu.getId(),
                                menu.getName()
                        )).toList()
                )).toList();

    }

    // 유저 최근 검색어 리스트로 반환
    public List<SearchHistoriesDto> getHistories(Long userId) {

        // 이메일로 로그인한 유저 바인딩
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 최근 검색 기록 10개를 찾아서 리스트로 변환 후 반환.
        return searchLogRepository.findTop10ByUserOrderBySearchedAtDesc(currentUser).stream()
                .map(log -> new SearchHistoriesDto(log.getKeyword()))
                .toList();
    }

    // 인기 검색어 리스트로 반환
    public List<PopularKeywordDto> getPopularKeywords() {

        Pageable top10 = PageRequest.of(0, 10); // 10개만 볼 수 있게 페이징
        List<String> keywords = searchLogRepository.findTop10PopularKeywords(top10); // 가장 많이 검색된 키워드를 담는 리스트

        // 순위와 인기 검색어를 담을 리스트
        List<PopularKeywordDto> list = new ArrayList<>();

        // 랭킹, 키워드 별로 바인딩
        for (int i = 0; i < keywords.size(); i++) {
            list.add(new PopularKeywordDto(i + 1L, keywords.get(i)));
        }

        return list;
    }

    // 내 검색 기록 삭제
    public void deleteHistories(User user) {

        if(user == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        searchLogRepository.deleteAllByUser(user);
    }

}
