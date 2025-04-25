package sparta.bunny.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.review.dto.request.OwnerCommentCreateRequestDto;
import sparta.bunny.domain.review.dto.request.OwnerCommentDeleteRequestDto;
import sparta.bunny.domain.review.dto.response.OwnerCommentCreateResponse;
import sparta.bunny.domain.review.service.OwnerReviewService;

@RestController
@RequestMapping("/owner-comments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class OwnerCommentController {

	private final OwnerReviewService ownerReviewService;

	/**
	 * 사장 리뷰 응답 생성
	 * @param request 생성할 리뷰 id, 내용
	 * @return 생성한 응답
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<OwnerCommentCreateResponse>> saveOwnerComment(
		@RequestBody OwnerCommentCreateRequestDto request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		CommonResponse<OwnerCommentCreateResponse> ownerCommentCreateResponseCommonResponse = ownerReviewService.saveOwnerComment(
			request, userDetails);
		return ResponseEntity.status(HttpStatus.CREATED).body(ownerCommentCreateResponseCommonResponse);
	}

	/**
	 * 사장 리뷰 응답 삭제
	 * @param dto 삭제할 응답 id
	 * @return 삭제 성공 여부
	 */
	@DeleteMapping
	public ResponseEntity<String> deleteOwnerComment(
		@RequestBody OwnerCommentDeleteRequestDto dto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		ownerReviewService.deleteOwnerComment(dto, userDetails);
		return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다");
	}
}
