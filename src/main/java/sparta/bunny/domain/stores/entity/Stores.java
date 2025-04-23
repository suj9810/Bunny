package sparta.bunny.domain.stores.entity;

import java.time.LocalTime;

import org.apache.catalina.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "stores")
public class Stores {

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

	// 가게 등록
	public Stores(User user, String storeName, String openTime, String closeTime, Integer minOrderPrice, String notice,
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
