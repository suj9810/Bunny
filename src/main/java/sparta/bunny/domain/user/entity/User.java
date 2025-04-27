package sparta.bunny.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.common.audit.BaseEntity;
import sparta.bunny.domain.order.entity.Order;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = true) // 소셜 로그인 사용자의 경우 비밀번호가 없기 때문에
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole userRole; // UserType이 겹쳐서 이름 수정

	// // 나중에 추가할 소셜 로그인
	// @Enumerated(EnumType.STRING)
	// @Column(nullable = true)
	// private SocialType socialType;

	@Column(nullable = false)
	private String userNumber;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Order> orderList = new ArrayList<>();

	@Builder
	public User(String email, String password, String nickname, UserRole userRole, String userNumber,
		Boolean isDeleted) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.userRole = userRole;
		this.userNumber = userNumber;
		this.isDeleted = isDeleted;
	}

	public void updateUserInfo(String nickname, String userNumber) {
		this.nickname = nickname;
		this.userNumber = userNumber;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void addOrder(Order order) {
		this.orderList.add(order);
		order.setUser(this); // FK 설정!
	}

	public void softDelete() {
		this.isDeleted = true;
	}
}
