package sparta.bunny.domain.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import sparta.bunny.domain.user.entity.User;

@Slf4j
@Component
public class TokenProvider {

	private final SecretKey key;
	private final long accessTokenExpiration;
	private final long refreshTokenExpiration;

	// 토큰 생성, 검증
	public TokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-expiration}") long accessTokenExpiration,
		@Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	// 유저 정보로 AccessToken
	public String createAccessToken(User user) {
		long now = System.currentTimeMillis();

		return Jwts.builder()
			.setSubject(String.valueOf(user.getId()))
			.claim("userRole", user.getUserRole().name())// 토큰 subject 설정
			.setIssuedAt(new Date(now)) // 토큰 생성 시간(현재 시각)
			.setExpiration(new Date(now + accessTokenExpiration)) // 만료 시간 : 1시간
			.signWith(key, SignatureAlgorithm.HS256) // 비밀키로 서명 (HS256 알고리즘)
			.compact(); // JWT 문자열로 변환
	}

	// 유저 정보로 RefreshToken
	public String createRefreshToken(User user) {
		long now = System.currentTimeMillis(); // 현재 시간 가져오기 -> 발급/만료시간 설정

		return Jwts.builder()
			.setSubject(String.valueOf(user.getId())) // 토큰 subject 설정
			.setIssuedAt(new Date(now)) // 토큰 생성 시간(현재 시각)
			.setExpiration(new Date(now + refreshTokenExpiration)) // 만료 시간 : 14일
			.signWith(key, SignatureAlgorithm.HS256) // 서명 키 + 알고리즘
			.compact(); // JWT 문자열 생성
	}

	// 토큰 유효성 검사
	public boolean validateToken(String token) {
		try {
			JwtParser parser = Jwts.parser().verifyWith(key).build();
			parser.parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.warn("잘못된 JWT 서명입니다."); // 서명 오류
		} catch (ExpiredJwtException e) {
			log.warn("만료된 JWT 토큰입니다."); // 토큰 만료
		} catch (UnsupportedJwtException e) {
			log.warn("지원되지 않는 JWT 토큰입니다."); // 잘못된 형식
		} catch (IllegalArgumentException e) {
			log.warn("JWT 토큰이 비어있습니다."); // 토큰 null
		}
		return false;
	}

	// 검증된 토큰 -> userId(Long) 반환
	public Long getUserIdFromToken(String token) {
		JwtParser parser = Jwts.parser() // JWT 해석하는 파서
			.verifyWith(key) // 서명에 사용한 SecretKey 사용 -> 검증 수행
			.build();

		Jws<Claims> claims = parser.parseSignedClaims(token);

		String subject = claims.getPayload().getSubject();

		return Long.parseLong(subject);
	}
}
