package sparta.bunny.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import sparta.bunny.domain.auth.dto.LoginRequestDto;
import sparta.bunny.domain.auth.dto.LoginResponseDto;
import sparta.bunny.domain.auth.dto.UserDeleteRequestDto;
import sparta.bunny.domain.auth.jwt.TokenProvider;
import sparta.bunny.domain.auth.repository.RefreshTokenRepository;
import sparta.bunny.domain.user.CustomPasswordEncoder;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;
import sparta.bunny.domain.user.exception.UserException;
import sparta.bunny.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@InjectMocks
	private AuthService authService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private CustomPasswordEncoder passwordEncoder;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Test
	@DisplayName("로그인 성공 테스트")
	void loginSuccess() {
		// given
		String email = "test@example.com";
		String password = "CorrectPassword123!";
		String encodedPassword = "encodedPassword";
		String accessToken = "mockAccessToken";
		String refreshToken = "mockRefreshToken";

		// 로그인 요청 Dto
		LoginRequestDto requestDto = new LoginRequestDto(email, password);

		// User 객체
		User user = User.builder()
			.id(1L)
			.email(email)
			.password(encodedPassword)
			.nickname("테스트유저")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(false)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user)); // 이메일로 유저 찾기
		when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true); // 비밀번호 일치
		when(tokenProvider.createAccessToken(user)).thenReturn(accessToken); // 액세스 토큰 발급
		when(tokenProvider.createRefreshToken(user)).thenReturn(refreshToken); // 리프레시 토큰 발급

		// when
		LoginResponseDto response = authService.login(requestDto);

		// then
		assertThat(response.getUserId()).isEqualTo(user.getId());
		assertThat(response.getUserEmail()).isEqualTo(user.getEmail());
		assertThat(response.getNickname()).isEqualTo(user.getNickname());
		assertThat(response.getAccessToken()).isEqualTo(accessToken);
		assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

		// 유저id, RefreshToken, 만료시간이 맞는지 확인
		verify(refreshTokenRepository).save(eq(user.getId()), eq(refreshToken), anyLong());
	}

	@Test
	@DisplayName("로그인 실패 - 유저 없음")
	void loginFailUserNotFound() {
		// given
		String email = "notfound@example.com";
		String password = "AnyPassword123!";
		LoginRequestDto request = new LoginRequestDto(email, password);

		// 이메일로 조회했을 때 빈값 반환
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		// when
		UserException exception = assertThrows(UserException.class, () -> authService.login(request));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 오류")
	void loginFailWrongPassword() {
		// given
		String email = "test@email.com";
		String correctPassword = "Test1234!";
		String wrongPassword = "Wrong1234!";

		LoginRequestDto requestDto = new LoginRequestDto(email, wrongPassword);

		User user = User.builder()
			.id(1L)
			.email(email)
			.password("encodedPassword")
			.nickname("테스트유저")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(false)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(wrongPassword, user.getPassword())).thenReturn(false);

		// when
		UserException exception = assertThrows(UserException.class, () -> authService.login(requestDto));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("이메일 또는 비밀번호가 올바르지 않습니다.");
	}

	@Test
	@DisplayName("로그아웃 성공 테스트")
	void logoutSuccess() {
		// given
		Long userId = 1L;
		String refreshToken = "mockRefreshToken";

		when(refreshTokenRepository.findByUserId(userId)).thenReturn(refreshToken);

		// when
		authService.logout(userId);

		// then
		verify(refreshTokenRepository).delete(userId);
	}

	@Test
	@DisplayName("로그아웃 실패 - 로그인 상태 아님")
	void logoutFailNotLoggedIn() {
		// given
		Long userId = 1L;

		// refreshToken 조회했는데 null 반환
		when(refreshTokenRepository.findByUserId(userId)).thenReturn(null);

		// when
		UserException exception = assertThrows(UserException.class, () -> authService.logout(userId));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("로그인이 필요합니다.");
	}

	@Test
	@DisplayName("회원탈퇴 성공 - 소프트 삭제")
	void deleteUser() {
		// given
		Long userId = 1L;
		String rawPassword = "CorrectPassword123!";

		User user = User.builder()
			.id(userId)
			.email("test@example.com")
			.password("encodedPassword")
			.nickname("테스트유저")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(false)
			.build();

		UserDeleteRequestDto requestDto = new UserDeleteRequestDto(rawPassword);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);

		// when
		authService.deleteUser(user, requestDto);

		// then
		assertThat(user.getIsDeleted()).isTrue(); // 소프트 삭제 되었는지 확인
		verify(redisTemplate).delete("RT:" + userId); // Redis RefreshToken 삭제 호출됐는지 확인
	}

	@Test
	@DisplayName("회원탈퇴 실패 - 비밀번호 불일치")
	void deleteUserFailWrongPassword() {
		// given
		Long userId = 1L;
		String wrongPassword = "WrongPassword123!";

		User user = User.builder()
			.id(userId)
			.email("test@example.com")
			.password("encodedPassword")
			.nickname("테스트유저")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(false)
			.build();

		UserDeleteRequestDto requestDto = new UserDeleteRequestDto(wrongPassword);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(wrongPassword, user.getPassword())).thenReturn(false);

		// when
		UserException exception = assertThrows(UserException.class, () -> authService.deleteUser(user, requestDto));

		// then
		assertThat(exception.getResponseCode().getMessage()).isEqualTo("현재 비밀번호가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("회원탈퇴 성공 - 하드 삭제")
	void hardDeleteUser() {
		// given
		Long userId = 1L;

		User user = User.builder()
			.id(userId)
			.email("test@example.com")
			.password("encodedPassword")
			.nickname("테스트유저")
			.userRole(UserRole.USER)
			.userNumber("010-1234-5678")
			.isDeleted(true) // 이미 소프트 삭제된 상태
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		authService.hardDeleteUser(userId);

		// then
		verify(userRepository).deleteById(userId); // 실제로 deleteById 호출됐는지
		verify(redisTemplate).delete("RT:" + userId); // Redis RefreshToken 삭제 호출됐는지
	}
}