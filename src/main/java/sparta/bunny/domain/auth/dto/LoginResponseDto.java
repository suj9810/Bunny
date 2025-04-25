package sparta.bunny.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
	private Long userId;
	private String userEmail;
	private String nickname;
	private String accessToken;
	private String refreshToken;
}
