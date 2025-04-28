package sparta.bunny.domain.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import sparta.bunny.domain.search.dto.response.PopularKeywordDto;
import sparta.bunny.domain.search.dto.response.SearchHistoriesDto;
import sparta.bunny.domain.search.entity.SearchLog;
import sparta.bunny.domain.search.repository.SearchLogRepository;
import sparta.bunny.domain.search.service.SearchService;

import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;
import sparta.bunny.domain.user.repository.UserRepository;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private SearchLogRepository searchLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;

    private User user;
    private String keyword;
    private Long userId;

    @BeforeEach
    public void setUp() {
        // given: 테스트용 유저, 키워드, userId
        user = User.builder()
                .id(1L)
                .email("example@example.com")
                .password("Abcd1234*")
                .nickname("장팔춘")
                .userRole(UserRole.USER)  // Role 지정
                .userNumber("010-1234-5678")
                .isDeleted(false)
                .build();
        keyword = "test";
        userId = 1L;
    }

    @Test
    void testGetAndSaveSearchLog() {

        // given

        SearchLog searchLog = new SearchLog(user, keyword);

        given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));
        given(searchLogRepository.save(any(SearchLog.class))).willReturn(searchLog);
        given(storeRepository.findByStoreNameContaining(keyword)).willReturn(List.of());

        // when
        searchService.getAndSaveSearchLog(userId, keyword);

        // then
        assertEquals(keyword, searchLog.getKeyword());
        assertEquals(user, searchLog.getUser());
        verify(userRepository, times(1)).findById(userId);
        verify(searchLogRepository).save(any(SearchLog.class));

    }

    @Test
    void testGetHistories() {

        // given
        SearchLog searchLog1 = new SearchLog(user, "testKeyword1");
        SearchLog searchLog2 = new SearchLog(user, "testKeyword2");

        given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));
        given(searchLogRepository.findTop10ByUserOrderBySearchedAtDesc(user)).willReturn(List.of(searchLog1, searchLog2));

        // when
        List<SearchHistoriesDto> histories = searchService.getHistories(userId);

        // then
        assertEquals(2, histories.size());
        assertEquals("testKeyword1", histories.get(0).getKeyword());
        assertEquals("testKeyword2", histories.get(1).getKeyword());

        verify(userRepository, times(1)).findById(userId);
        verify(searchLogRepository, times(1)).findTop10ByUserOrderBySearchedAtDesc(user);
    }

    @Test
    public void testGetPopularKeywords() {
        // given
        List<String> popularKeywords = List.of("keyword1", "keyword2", "keyword3", "keyword4", "keyword5",
                "keyword6", "keyword7", "keyword8", "keyword9", "keyword10");
        given(searchLogRepository.findTop10PopularKeywords(PageRequest.of(0, 10)))
                .willReturn(popularKeywords);

        // when
        List<PopularKeywordDto> result = searchService.getPopularKeywords();

        // then
        assertEquals(10, result.size());
        for (int i = 0; i < result.size(); i++) {
            PopularKeywordDto dto = result.get(i);
            assertEquals(Long.valueOf(i + 1), dto.getRank());
            assertEquals(popularKeywords.get(i), dto.getKeyword());
        }

        verify(searchLogRepository, times(1)).findTop10PopularKeywords(PageRequest.of(0, 10));

    }

    @Test
    public void testDeleteHistories() {

        // given
        given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));

        // when
        searchService.getAndSaveSearchLog(user.getId(), keyword);
        searchService.deleteHistories(user);

        // then
        verify(searchLogRepository, times(1)).deleteAllByUser(user);
    }

}
