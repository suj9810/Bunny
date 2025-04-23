package sparta.bunny.domain.review.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.review.code.ReviewSuccessCode;
import sparta.bunny.domain.review.dto.response.ReviewCreateResponse;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public CommonResponse saveReview(String content) {

		Review review = Review.builder()
			.content(content)
			.build();

		Review saved = reviewRepository.save(review);

		ReviewCreateResponse createdReview = ReviewCreateResponse.builder().reviewId(saved.getId()).build();

		return CommonResponse.of(ReviewSuccessCode.REVIEW_CREATE_SUCCESS, createdReview);
	}
}
