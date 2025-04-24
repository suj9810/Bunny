package sparta.bunny.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {
	private final Long userId;
	private final String userEmail;
	private final String userNum;
	private final String nickName;

	@Builder
	public UserResponseDto(Long userId, String userEmail, String userNum, String nickName) {
		this.userId = userId;
		this.userEmail = userEmail;
		this.userNum = userNum;
		this.nickName = nickName;
	}
}
