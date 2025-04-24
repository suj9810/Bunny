package sparta.bunny.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.search.repository.SearchLogRepository;
import sparta.bunny.domain.stores.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchLogRepository searchLogRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    // 검색 결과 조회 및 기록 저장

    // 유저 최근 검색어 리스트로 반환

    // 인기 검색어 리스트로 반환


}
