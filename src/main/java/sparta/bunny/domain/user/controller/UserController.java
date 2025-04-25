package sparta.bunny.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.user.code.UserResponseCode;
import sparta.bunny.domain.user.dto.request.UserSignUpRequestDto;
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

}
