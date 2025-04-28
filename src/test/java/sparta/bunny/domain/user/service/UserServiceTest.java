package sparta.bunny.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sparta.bunny.domain.user.CustomPasswordEncoder;
import sparta.bunny.domain.user.dto.request.UserPasswordUpdateRequestDto;
import sparta.bunny.domain.user.dto.request.UserUpdateRequestDto;
import sparta.bunny.domain.user.dto.response.UserResponseDto;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;
import sparta.bunny.domain.user.exception.UserException;
import sparta.bunny.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CustomPasswordEncoder passwordEncoder;

	private User createUser(Long userId) {
		return User.builder()
			.id(userId)
			.email("test@example.com")
			.password("encodedPassword")
			.nickname("테스트유저")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(false)
			.build();
	}

	@Test
	@DisplayName("회원 조회 성공")
	void getUserByIdSuccess() {
		// given
		Long userId = 1L;
		User user = createUser(userId);

		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when
		UserResponseDto response = userService.getUserById(userId);

		// then
		assertThat(response.getUserId()).isEqualTo(user.getId());
		assertThat(response.getUserEmail()).isEqualTo(user.getEmail());
		assertThat(response.getNickName()).isEqualTo(user.getNickname());
		assertThat(response.getUserNum()).isEqualTo(user.getUserNumber());
	}

	@Test
	@DisplayName("회원 조회 실패 - 유저 없음")
	void getUserByIdFailUserNotFound() {
		// given
		Long userId = 1L;

		given(userRepository.findById(userId)).willReturn(Optional.empty()); // 유저 없음 설정

		// when
		UserException exception = assertThrows(UserException.class, () -> userService.getUserById(userId));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	void updateUserInfoSuccess() {
		// given
		Long userId = 1L;
		User user = createUser(userId);

		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("새닉네임", "010-9999-9999");

		// when
		UserUpdateRequestDto responseDto = userService.updateUserInfo(user, requestDto);

		// then
		assertThat(user.getNickname()).isEqualTo("새닉네임");
		assertThat(user.getUserNumber()).isEqualTo("010-9999-9999");
		assertThat(responseDto.getNickname()).isEqualTo("새닉네임");
		assertThat(responseDto.getUserNumber()).isEqualTo("010-9999-9999");
	}

	@Test
	@DisplayName("비밀번호 수정 성공")
	void updatePasswordSuccess() {
		// given
		Long userId = 1L;
		User user = createUser(userId);

		String currentRawPassword = "CorrectPassword123!";
		String currentEncodedPassword = "encodedCurrentPassword";
		String newRawPassword = "NewPassword123!";
		String newEncodedPassword = "encodedNewPassword";

		// 현재 비밀번호 세팅 = 암호화된 비밀번호
		user.updatePassword(currentEncodedPassword);
		UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(currentRawPassword, newRawPassword);

		// 입력한 비밀번호 == 현재 암호화된 비밀번호(강제)
		given(passwordEncoder.matches(currentRawPassword, currentEncodedPassword)).willReturn(true);
		// 현재 암호화된 비밀번호 != 새 비밀번호(강제)
		given(passwordEncoder.matches(newRawPassword, currentEncodedPassword)).willReturn(false);
		// 새 비밀번호 -> 새로 암호화된 비밀번호
		given(passwordEncoder.encode(newRawPassword)).willReturn(newEncodedPassword);

		// when
		userService.updatePassword(user, requestDto);

		// then - 암호화된 새 비밀번호로 잘 저장됐는지, 저장된 것도 호출 됐는지 확인
		assertThat(user.getPassword()).isEqualTo(newEncodedPassword);
		verify(userRepository).save(user);
	}

	@Test
	@DisplayName("비밀번호 수정 실패 - 현재 비밀번호 불일치")
	void updatePasswordFailWrongCurrentPassword() {
		// given
		Long userId = 1L;
		User user = createUser(userId);

		String wrongCurrentPassword = "WrongPassword123!";
		String storedPassword = "encodedStoredPassword";
		String newPassword = "NewPassword123!";

		user.updatePassword(storedPassword);

		UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(wrongCurrentPassword, newPassword);

		// 저장된 비밀번호 != 현재 비밀번호(강제)
		given(passwordEncoder.matches(wrongCurrentPassword, storedPassword)).willReturn(false);

		// when
		UserException exception = assertThrows(UserException.class, () -> userService.updatePassword(user, requestDto));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("현재 비밀번호가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("비밀번호 수정 실패 - 새 비밀번호가 현재 비밀번호와 같음")
	void updatePasswordFailSameAsCurrentPassword() {
		// given
		Long userId = 1L;
		User user = createUser(userId);

		String currentRawPassword = "SamePassword123!";
		String currentEncodedPassword = "encodedSamePassword";

		user.updatePassword(currentEncodedPassword);

		UserPasswordUpdateRequestDto requestDto
			= new UserPasswordUpdateRequestDto(currentRawPassword, currentRawPassword); // 새 비번 = 현재 비번

		// 입력한 비밀번호 == 저장된 비밀번호
		given(passwordEncoder.matches(currentRawPassword, currentEncodedPassword)).willReturn(true); // 현재 비번 일치
		given(passwordEncoder.matches(currentRawPassword, currentEncodedPassword)).willReturn(true); // 새 비번도 일치하게

		// when
		UserException exception = assertThrows(UserException.class, () -> userService.updatePassword(user, requestDto));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다");
	}

}