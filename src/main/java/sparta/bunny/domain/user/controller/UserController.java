package sparta.bunny.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.user.code.UserResponseCode;
import sparta.bunny.domain.user.dto.request.UserPasswordUpdateRequestDto;
import sparta.bunny.domain.user.dto.request.UserSignUpRequestDto;
import sparta.bunny.domain.user.dto.request.UserUpdateRequestDto;
import sparta.bunny.domain.user.dto.response.UserResponseDto;
import sparta.bunny.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	/**
	 * 회원가입
	 * @param requestDto
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<Object>> signup(
		@Valid @RequestBody UserSignUpRequestDto requestDto) {

		UserResponseDto response = userService.signup(requestDto);
		UserResponseCode code = UserResponseCode.SIGNUP_SUCCESS;

		return ResponseEntity.status(code.getHttpStatus())
			.body(CommonResponse.of(code, response));
	}

	/**
	 * 회원 조회
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<CommonResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
		UserResponseDto data = userService.getUserById(id);
		UserResponseCode code = UserResponseCode.FIND_USER_SUCCESS;

		return ResponseEntity.status(code.getHttpStatus())
			.body(CommonResponse.of(code, data));
	}

	/**
	 * 정보 수정
	 * @param requestDto
	 * @param userDetails
	 * @return
	 */
	@PutMapping("/me")
	public ResponseEntity<CommonResponse<UserUpdateRequestDto>> updateUserInfo(
		@Valid @RequestBody UserUpdateRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		UserUpdateRequestDto updatedUser = userService.updateUserInfo(userDetails.getUser(), requestDto);

		return ResponseEntity.ok(
			CommonResponse.of(UserResponseCode.UPDATE_USER_SUCCESS, updatedUser)
		);
	}

	/**
	 * 비밀번호 업데이트
	 * @param requestDto
	 * @param userDetails
	 * @return
	 */
	@PatchMapping("/{id}/update-password")
	public ResponseEntity<CommonResponse<Void>> updatePassword(
		@Valid @RequestBody UserPasswordUpdateRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		userService.updatePassword(userDetails.getUser(), requestDto);
		return ResponseEntity.ok(
			CommonResponse.of(UserResponseCode.UPDATE_PASSWORD_SUCCESS, null)
		);
	}

}
