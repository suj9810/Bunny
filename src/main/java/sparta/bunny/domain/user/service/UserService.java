package sparta.bunny.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.user.CustomPasswordEncoder;
import sparta.bunny.domain.user.code.UserErrorCode;
import sparta.bunny.domain.user.dto.request.UserPasswordUpdateRequestDto;
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
	private final EntityManager entityManager;
	// private final RedisTemplate<String, String> redisTemplate;

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

	/**
	 * 회원 수정
	 * @param user
	 * @param requestDto
	 * @return
	 */
	@Transactional
	public UserUpdateRequestDto updateUserInfo(User user, UserUpdateRequestDto requestDto) {
		user.updateUserInfo(requestDto.getNickname(), requestDto.getUserNumber());

		return requestDto;
	}

	/**
	 * 비밀번호 수정
	 * @param user
	 * @param requestDto
	 */
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

		userRepository.save(user);
	}

}