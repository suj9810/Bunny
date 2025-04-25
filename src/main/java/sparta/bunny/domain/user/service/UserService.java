package sparta.bunny.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.user.CustomPasswordEncoder;
import sparta.bunny.domain.user.code.UserErrorCode;
import sparta.bunny.domain.user.dto.request.UserPasswordUpdateRequestDto;
import sparta.bunny.domain.user.dto.request.UserSignUpRequestDto;
import sparta.bunny.domain.user.dto.request.UserUpdateRequestDto;
import sparta.bunny.domain.user.dto.response.UserResponseDto;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.exception.UserException;
import sparta.bunny.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final CustomPasswordEncoder passwordEncoder;

	/**
	 * 회원가입
	 *
	 * @param request
	 * @return
	 */
	@Transactional
	public UserResponseDto signup(UserSignUpRequestDto request) {

		// 이메일 중복 검사
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
		}

		// 비밀번호 유효성 검증
		validatePassword(request.getPassword());

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 유저 생성 및 저장
		User user = User.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.nickname(request.getNickname())
			.userRole(request.getUserRole())
			.userNumber(request.getUserNumber())
			.isDeleted(false)
			.build();

		User savedUser = userRepository.save(user);

		return UserResponseDto.builder()
			.userId(savedUser.getId())
			.userEmail(savedUser.getEmail())
			.userNum(savedUser.getUserNumber())
			.nickName(savedUser.getNickname())
			.build();
	}

	/**
	 * 비밀번호 유효성 검사 메서드
	 * @param password
	 */
	private void validatePassword(String password) {
		if (password.length() < 8 ||
			!password.matches(".*[A-Z].*") ||
			!password.matches(".*[a-z].*") ||
			!password.matches(".*\\d.*") ||
			!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
			throw new UserException(UserErrorCode.INVALID_PASSWORD_FORMAT);
		}
	}

	/**
	 * 회원 조회
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public UserResponseDto getUserById(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		return UserResponseDto.builder()
			.userId(user.getId())
			.userEmail(user.getEmail())
			.userNum(user.getUserNumber())
			.nickName(user.getNickname())
			.build();
	}

	@Transactional
	public UserUpdateRequestDto updateUserInfo(User user, UserUpdateRequestDto requestDto) {
		user.updateUserInfo(requestDto.getNickname(), requestDto.getUserNumber());

		return requestDto;
	}

	@Transactional
	public void updatePassword(User user, UserPasswordUpdateRequestDto requestDto) {

		// 현재 비밀번호 != 입력한 비밀번호
		if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
			throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
		}

		// 현재 비밀번호 == 새 비밀번호
		if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
			throw new UserException(UserErrorCode.SAME_AS_PASSWORD);
		}

		// 새 비밀번호 암호화
		String encodedNewPassword = passwordEncoder.encode(requestDto.getNewPassword());
		user.updatePassword(encodedNewPassword);
	}
}