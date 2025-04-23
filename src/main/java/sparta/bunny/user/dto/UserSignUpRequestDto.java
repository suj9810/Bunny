package sparta.bunny.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import sparta.bunny.user.entity.UserRole;

@Getter
public class UserSignUpRequestDto {

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;

	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;

	@NotBlank(message = "회원 구분은 필수입니다.")
	private UserRole userRole;

	@NotBlank(message = "전화번호는 필수입니다.")
	private String userNumber;
}
