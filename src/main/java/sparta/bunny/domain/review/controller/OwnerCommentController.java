package sparta.bunny.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.review.dto.request.OwnerCommentCreateRequestDto;
import sparta.bunny.domain.review.dto.response.OwnerCommentCreateResponse;
import sparta.bunny.domain.review.service.OwnerReviewService;

@RestController
@RequestMapping("/owner-comments")
@RequiredArgsConstructor
public class OwnerCommentController {

	private final OwnerReviewService ownerReviewService;

	@PostMapping
	public ResponseEntity<CommonResponse<OwnerCommentCreateResponse>> saveOwnerComment(
		@RequestBody OwnerCommentCreateRequestDto request) {
		CommonResponse<OwnerCommentCreateResponse> ownerCommentCreateResponseCommonResponse = ownerReviewService.saveOwnerComment(
			request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ownerCommentCreateResponseCommonResponse);
	}
}
