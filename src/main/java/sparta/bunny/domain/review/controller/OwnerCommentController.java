package sparta.bunny.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class OwnerCommentController {

	private final OwnerReviewService ownerReviewService;

	@PostMapping
	public ResponseEntity<CommonResponse<OwnerCommentCreateResponse>> saveOwnerComment(
		@RequestBody OwnerCommentCreateRequestDto request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		CommonResponse<OwnerCommentCreateResponse> ownerCommentCreateResponseCommonResponse = ownerReviewService.saveOwnerComment(
			request, userDetails);
		return ResponseEntity.status(HttpStatus.CREATED).body(ownerCommentCreateResponseCommonResponse);
	}

	@DeleteMapping
	public ResponseEntity<String> deleteOwnerComment(
		@RequestBody OwnerCommentDeleteRequestDto dto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		ownerReviewService.deleteOwnerComment(dto, userDetails);
		return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다");
	}
}
