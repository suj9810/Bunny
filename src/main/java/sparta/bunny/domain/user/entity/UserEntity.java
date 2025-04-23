package sparta.bunny.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.common.audit.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, unique = true)
	private String userEmail;

	@Column(nullable = true) // 소셜 로그인 사용자의 경우 비밀번호가 없기 때문에
	private String userPassword;

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

	@Column(nullable = false)
	private Boolean isDeleted = false;

	@Builder
	public UserEntity(String userEmail, String userPassword, String nickname, UserRole userRole, String userNumber,
		Boolean isDeleted) {
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.nickname = nickname;
		this.userRole = userRole;
		this.userNumber = userNumber;
		this.isDeleted = isDeleted;
	}
}
