package sparta.bunny.domain.review.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.domain.review.code.ReviewSuccessCode;
import sparta.bunny.domain.review.dto.request.ReviewCreateRequest;
import sparta.bunny.domain.review.dto.response.ReviewCreateResponse;
import sparta.bunny.domain.review.dto.response.ReviewFindResponse;
import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final OwnerCommentRepository ownerCommentRepository;

	@Transactional
	public CommonResponse<ReviewCreateResponse> saveReview(ReviewCreateRequest request) {

		Review review = Review.builder()
			.content(request.getContent())
			.rating(request.getRating())
			.build();

		Review saved = reviewRepository.save(review);

		ReviewCreateResponse createdReview = ReviewCreateResponse.builder()
			.reviewId(saved.getId())
			.build();

		return CommonResponse.of(ReviewSuccessCode.REVIEW_CREATE_SUCCESS, createdReview);
	}

	public CommonResponses<ReviewFindResponse> getReviewsByStoreId(Long storeId, Pageable pageable) {
		Page<Review> page = reviewRepository.findByStoresId(storeId, pageable);

		List<Review> reviews = page.getContent();

		List<ReviewFindResponse> responses = new ArrayList<>();

		for (Review review : reviews) {
			OwnerComment ownerComment = ownerCommentRepository.findByReviewId(review.getId()).orElse(null);
			responses.add(ReviewFindResponse.from(review, ownerComment));
		}

		Page<ReviewFindResponse> responsePage = new PageImpl<>(
			responses,
			pageable,
			page.getTotalElements()
		);

		return CommonResponses.of(
			ReviewSuccessCode.REVIEW_FOUND_SUCCESS,
			responsePage
		);
	}
}
