package sparta.bunny.domain.stores.entity;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.user.entity.User;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String storeName;

    private LocalTime openTime;

    private LocalTime closeTime;

    private Integer minOrderPrice;

    private String notice;

    private Boolean isClosed;

    private String categoryName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    private List<Menu> menus;

    // 가게 등록
    @Builder
    public Store(User user, String storeName, String openTime, String closeTime, Integer minOrderPrice, String notice,
                 Boolean isClosed, String categoryName) {
        this.user = user;
        this.storeName = storeName;
        this.openTime = LocalTime.parse(openTime);
        this.closeTime = LocalTime.parse(closeTime);
        this.minOrderPrice = minOrderPrice;
        this.notice = notice;
        this.isClosed = false;
        this.categoryName = categoryName;
    }

    // 가게 수정
    public void updateStore(String storeName, String openTime, String closeTime, Integer minOrderPrice, String notice,
                            String categoryName) {
        this.storeName = storeName;
        this.openTime = LocalTime.parse(openTime);
        this.closeTime = LocalTime.parse(closeTime);
        this.minOrderPrice = minOrderPrice;
        this.notice = notice;
        this.categoryName = categoryName;
    }

    // 가게 폐업
    public void close() {
        this.isClosed = true;
    }

    //가게 폐업 해제
    public void reopen() {
        this.isClosed = false;
    }

}
