package sparta.bunny.domain.review.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.review.code.ReviewExceptionCode;
import sparta.bunny.domain.review.code.ReviewSuccessCode;
import sparta.bunny.domain.review.dto.request.OwnerCommentCreateRequestDto;
import sparta.bunny.domain.review.dto.response.OwnerCommentCreateResponse;
import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.exception.ReviewException;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class OwnerReviewService {

	private final OwnerCommentRepository ownerCommentRepository;
	private final ReviewRepository reviewRepository;

	@Transactional
	public CommonResponse<OwnerCommentCreateResponse> saveOwnerComment(OwnerCommentCreateRequestDto request) {

		Review review = reviewRepository.findById(request.getReviewId()).orElseThrow(() -> new ReviewException(
			ReviewExceptionCode.REVIEW_NOT_FOUND));

		OwnerComment ownerComment = OwnerComment.builder()
			.review(review)
			.content(request.getContent())
			.build();

		OwnerComment save = ownerCommentRepository.save(ownerComment);

		OwnerCommentCreateResponse response = OwnerCommentCreateResponse.builder()
			.ownerReviewId(save.getId())
			.build();

		return CommonResponse.of(ReviewSuccessCode.REVIEW_COMMENT_CREATE_SUCCESS, response);
	}
}
