package sparta.bunny.domain.search.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sparta.bunny.domain.search.entity.SearchLog;
import sparta.bunny.domain.user.entity.User;

import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    // 유저 최근 검색기록 10개 조회
    @EntityGraph(attributePaths = "user")
    List<SearchLog> findTop10ByUserOrderBySearchedAtDesc(User user);

    // 인기 검색어 10개 조회
    @Query("""
        SELECT s.keyword
        FROM SearchLog s
        GROUP BY s.keyword
        ORDER BY COUNT(s.keyword) DESC
    """)
    List<String> findTop10PopularKeywords(Pageable pageable);

}
