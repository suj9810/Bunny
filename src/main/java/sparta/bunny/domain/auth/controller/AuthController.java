package sparta.bunny.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.dto.LoginRequestDto;
import sparta.bunny.domain.auth.dto.LoginResponseDto;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.auth.service.AuthService;
import sparta.bunny.domain.user.code.UserResponseCode;
import sparta.bunny.domain.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<CommonResponse> login(@RequestBody LoginRequestDto dto) {
		LoginResponseDto response = authService.login(dto);

		return ResponseEntity.ok(
			CommonResponse.of(UserResponseCode.LOGIN_SUCCESS, response)
		);
	}

	@PostMapping("/logout")
	public ResponseEntity<CommonResponse<?>> logout(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		authService.logout(user.getId());
		return ResponseEntity.ok(CommonResponse.of(UserResponseCode.LOGOUT_SUCCESS, null));
	}
}
