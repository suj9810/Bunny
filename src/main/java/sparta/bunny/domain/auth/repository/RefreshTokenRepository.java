package sparta.bunny.domain.auth.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository {
	// Redis 접근
	private final RedisTemplate<String, String> redisTemplate;

	// key 값 구분용
	private static final String PREFIX = "RT:";

	// 로그인 성공 -> Redis에 RefreshToken 저장
	public void save(Long userId, String refreshToken, long expirationMillis) {
		redisTemplate.opsForValue().set(PREFIX + userId, refreshToken, Duration.ofMillis(expirationMillis));
	}

	// 재로그인 -> 저장된 토큰을 조회
	public String findByUserId(Long userId) {
		return redisTemplate.opsForValue().get(PREFIX + userId);
	}

	// 로그아웃 -> RefreshToken 삭제
	public void delete(Long userId) {
		redisTemplate.delete(PREFIX + userId);
	}
}
