package sparta.bunny.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordUpdateRequestDto {

	@NotBlank(message = "현재 비밀번호를 입력해 주세요.")
	private String oldPassword;

	@NotBlank(message = "새 비밀번호를 입력해 주세요.")
	private String newPassword;

}
