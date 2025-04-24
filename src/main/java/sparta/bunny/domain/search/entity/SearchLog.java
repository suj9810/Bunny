package sparta.bunny.domain.search.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "search_logs")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user; // 유저 FK

    @Column(nullable = false)
    private String keyword; // 검색 단어

    @Column(nullable = false, updatable = false)
    private LocalDateTime searchedAt; // 검색한 시점

    // DB 저장 전, 현재 시간 값으로 초기화
    @PrePersist
    public void setSearchedAt() {
        this.searchedAt = LocalDateTime.now();
    }


}
