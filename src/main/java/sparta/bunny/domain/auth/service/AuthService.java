package sparta.bunny.domain.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.auth.dto.LoginRequestDto;
import sparta.bunny.domain.auth.dto.LoginResponseDto;
import sparta.bunny.domain.auth.jwt.TokenProvider;
import sparta.bunny.domain.auth.repository.RefreshTokenRepository;
import sparta.bunny.domain.user.CustomPasswordEncoder;
import sparta.bunny.domain.user.code.UserErrorCode;
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

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

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
		refreshTokenRepository.delete(userId);
	}
}
