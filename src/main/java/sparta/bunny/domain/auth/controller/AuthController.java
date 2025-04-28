package sparta.bunny.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.dto.LoginRequestDto;
import sparta.bunny.domain.auth.dto.LoginResponseDto;
import sparta.bunny.domain.auth.dto.UserDeleteRequestDto;
import sparta.bunny.domain.auth.dto.UserSignUpRequestDto;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.auth.service.AuthService;
import sparta.bunny.domain.user.code.UserResponseCode;
import sparta.bunny.domain.user.dto.response.UserResponseDto;
import sparta.bunny.domain.user.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
public class AuthController {

	private final AuthService authService;

	/**
	 * 회원가입
	 * @param requestDto
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<Object>> signup(
		@Valid @RequestBody UserSignUpRequestDto requestDto) {

		UserResponseDto response = authService.signup(requestDto);
		UserResponseCode code = UserResponseCode.SIGNUP_SUCCESS;

		return ResponseEntity.status(code.getHttpStatus())
			.body(CommonResponse.of(code, response));
	}

	/**
	 * 로그인
	 * @param requestDto
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<CommonResponse> login(
		@RequestBody LoginRequestDto requestDto) {
		LoginResponseDto response = authService.login(requestDto);

		return ResponseEntity.ok(
			CommonResponse.of(UserResponseCode.LOGIN_SUCCESS, response)
		);
	}

	/**
	 * 로그아웃
	 * @param userDetails
	 * @return
	 */
	@PostMapping("/logout")
	public ResponseEntity<CommonResponse<?>> logout(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		User user = userDetails.getUser();
		authService.logout(user.getId());
		return ResponseEntity.ok(CommonResponse.of(UserResponseCode.LOGOUT_SUCCESS, null));
	}

	@DeleteMapping("/me")
	public ResponseEntity<CommonResponse<Void>> deleteUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody UserDeleteRequestDto requestDto
	) {
		authService.deleteUser(userDetails.getUser(), requestDto);

		return ResponseEntity.ok(
			CommonResponse.of(UserResponseCode.DELETE_USER_SUCCESS, null)
		);
	}

}
