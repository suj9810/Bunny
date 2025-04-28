package sparta.bunny.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.auth.dto.LoginRequestDto;
import sparta.bunny.domain.auth.dto.LoginResponseDto;
import sparta.bunny.domain.auth.dto.UserDeleteRequestDto;
import sparta.bunny.domain.auth.dto.UserSignUpRequestDto;
import sparta.bunny.domain.auth.jwt.TokenProvider;
import sparta.bunny.domain.auth.repository.RefreshTokenRepository;
import sparta.bunny.domain.user.CustomPasswordEncoder;
import sparta.bunny.domain.user.code.UserErrorCode;
import sparta.bunny.domain.user.dto.response.UserResponseDto;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.exception.UserException;
import sparta.bunny.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final CustomPasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisTemplate<String, String> redisTemplate;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	/**
	 * 회원가입
	 *
	 * @param request
	 * @return
	 */
	@Transactional
	public UserResponseDto signup(UserSignUpRequestDto request) {

		// 이메일 중복 검사
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
		}

		// 비밀번호 유효성 검증
		validatePassword(request.getPassword());

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 유저 생성 및 저장
		User user = User.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.nickname(request.getNickname())
			.userRole(request.getUserRole())
			.userNumber(request.getUserNumber())
			.isDeleted(false)
			.build();

		User savedUser = userRepository.save(user);

		return UserResponseDto.builder()
			.userId(savedUser.getId())
			.userEmail(savedUser.getEmail())
			.userNum(savedUser.getUserNumber())
			.nickName(savedUser.getNickname())
			.build();
	}

	/**
	 * 비밀번호 유효성 검사 메서드
	 * @param password
	 */
	private void validatePassword(String password) {
		if (password.length() < 8 ||
			!password.matches(".*[A-Z].*") ||
			!password.matches(".*[a-z].*") ||
			!password.matches(".*\\d.*") ||
			!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
			throw new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT);
		}
	}

	@Transactional
	public LoginResponseDto login(LoginRequestDto dto) {

		// 사용자 조회
		User user = userRepository.findByEmail(dto.getEmail())
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 비밀번호 검증
		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new UserException(UserErrorCode.LOGIN_FAILED);
		}

		// 토큰 생성
		String accessToken = tokenProvider.createAccessToken(user);
		String refreshToken = tokenProvider.createRefreshToken(user);

		// Redis 저장
		refreshTokenRepository.save(user.getId(), refreshToken, refreshTokenExpiration);

		// 응답
		return LoginResponseDto.builder()
			.userId(user.getId())
			.userEmail(user.getEmail())
			.nickname(user.getNickname())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout(Long userId) {
		// 유저 Id 조회
		String refreshToken = refreshTokenRepository.findByUserId(userId);
		if (refreshToken == null) {
			throw new UserException(UserErrorCode.LOGOUT_FAILED);
		}
		refreshTokenRepository.delete(userId);
	}

	// 회원 탈퇴 - Soft Deleted
	@Transactional
	public void deleteUser(User user, UserDeleteRequestDto requestDto) {

		// User 조회
		User realUser = userRepository.findById(user.getId())
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 비밀번호 비교
		if (!passwordEncoder.matches(requestDto.getPassword(), realUser.getPassword())) {
			throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
		}

		// 소프트 삭제 + 변경 저장
		realUser.softDelete();
		userRepository.save(realUser);
		userRepository.flush();

		// Redis에서 RT 삭제
		String key = "RT:" + user.getId();
		redisTemplate.delete(key);
	}

	// 회원 탈퇴 - Hard Deleted
	@Transactional
	public void hardDeleteUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 소프트 삭제 상태인지 검증
		if (!user.getIsDeleted()) {
			throw new UserException(UserErrorCode.USER_NOT_SOFT_DELETED);
		}

		// 진짜 하드 삭제 쿼리 호출
		userRepository.deleteById(userId);
		userRepository.flush();

		// Redis RT 삭제
		String key = "RT:" + userId;
		redisTemplate.delete(key);
	}
}
