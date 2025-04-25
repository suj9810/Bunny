package sparta.bunny.domain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.auth.jwt.JwtAuthenticationFilter;
import sparta.bunny.domain.auth.jwt.TokenProvider;
import sparta.bunny.domain.user.repository.UserRepository;

// @Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthSecurityConfig {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 등록 경로 : 인증 없이 접근 허용
				// 접근 허용하고 싶으면 경로 추가 💡
				.requestMatchers("/users/signup", "/auth/login", "/users/{id}").permitAll()
				// 그이 경로 : 반드시 인증 진행
				.anyRequest().authenticated()
			)

			.addFilterBefore(
				new JwtAuthenticationFilter(tokenProvider, userRepository),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}
}
