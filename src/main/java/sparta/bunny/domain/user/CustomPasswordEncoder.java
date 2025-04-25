package sparta.bunny.domain.user;

import org.springframework.stereotype.Component;

import at.favre.lib.crypto.bcrypt.BCrypt;

// 비밀번호 암호화
@Component
public class CustomPasswordEncoder {
	public String encode(String rawPassword) {
		return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
	}

	public boolean matches(String rawPassword, String encodedPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
		return result.verified;
	}
}
