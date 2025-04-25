package sparta.bunny.domain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class AuthSecurityConfig {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 등록 경로 : 인증 없이 접근 허용
				// 접근 허용하고 싶으면 경로 추가 💡
				// 비회원도 접근 가능한 경로
				.requestMatchers("/users/signup", "/auth/login", "/users/{id}").permitAll()
				//
				// // 로그인만 되어 있으면 가능
				// .requestMatchers("/**").hasAnyRole("USER", "OWNER")
				//
				// // 점주 만 접근 가능
				// .requestMatchers("/owner/**").hasAnyRole("OWNER")

				// 그외 경로 : 반드시 인증 진행
				.anyRequest().authenticated())

			.addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userRepository),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
