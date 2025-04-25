package sparta.bunny.domain.auth.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.user.code.UserErrorCode;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null) {
			if (tokenProvider.validateToken(token)) {
				Long userId = tokenProvider.getUserIdFromToken(token);
				User user = userRepository.findById(userId).orElse(null);

				if (user != null) {
					UserDetailsImpl userDetails = new UserDetailsImpl(user);
					UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} else {
				// 유효하지 않은 토큰
				setErrorResponse(response, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private void setErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");

		ObjectMapper objectMapper = new ObjectMapper();
		CommonResponse<?> errorResponse = CommonResponse.of(UserErrorCode.UNAUTHORIZED_ROLE, null);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}

	private String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if (bearer != null && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);  // "Bearer " 부분을 잘라내고 토큰만 반환
		}
		return null;
	}
}
