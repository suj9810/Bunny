package sparta.bunny.domain.stores.entity;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

	private String storeName; // 가게 이름

	private LocalTime openTime; // 오픈 시간

	private LocalTime closeTime; // 닫는 시간

	private Integer minOrderPrice; // 최소 금액

	private String notice; // 가게 공지

	private Boolean isClosed; // 폐업 여부

	private String categoryName; // 카테고리 (ex. 한식, 카페, 양식, 일식 등등)

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
		this.isClosed = isClosed;
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
