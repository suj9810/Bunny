package sparta.bunny.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	private static final String[] AUTH_WHITELIST = {
		"/users/signup", "/auth/login", "/users/{id}", "/searches", "/searches/trending"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// csrf, cors 설정
		http.csrf((csrf) -> csrf.disable());
		http.cors(Customizer.withDefaults());

		// 세션 관리 상태 없음
		http.sessionManagement(
			sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// formLogin, httpBasic 비활성화
		http.formLogin((form) -> form.disable());
		http.httpBasic(AbstractHttpConfigurer::disable);

		// Filter 적용
		http.addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userRepository),
			UsernamePasswordAuthenticationFilter.class);

		http.authorizeHttpRequests(auth -> auth
			.requestMatchers(AUTH_WHITELIST).permitAll() // 비회원도 접근 가능한 경로, 인증 없이 접근 허용
			.anyRequest().authenticated() // 그외 경로 : 반드시 인증 진행
		);

		return http.build();
	}
}
