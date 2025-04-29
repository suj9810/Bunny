package sparta.bunny.domain.review.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.common.service.FileService;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.order.code.OrderExceptionCode;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.order.exception.OrderException;
import sparta.bunny.domain.order.repository.OrderRepository;
import sparta.bunny.domain.review.code.ReviewExceptionCode;
import sparta.bunny.domain.review.code.ReviewSuccessCode;
import sparta.bunny.domain.review.dto.request.ReviewCreateRequest;
import sparta.bunny.domain.review.dto.request.ReviewDeleteRequestDto;
import sparta.bunny.domain.review.dto.response.ReviewCreateResponse;
import sparta.bunny.domain.review.dto.response.ReviewFindResponse;
import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.entity.ReviewImage;
import sparta.bunny.domain.review.exception.ReviewException;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewImageRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final OwnerCommentRepository ownerCommentRepository;
	private final OrderRepository orderRepository;

	// S3
	private final ReviewImageRepository reviewImageRepository;

	private final FileService fileService;

	@Transactional
	@PreAuthorize("hasRole('USER')")
	public CommonResponse<ReviewCreateResponse> saveReview(
		ReviewCreateRequest request, UserDetailsImpl userDetails
	) throws IOException {

		Order order = orderRepository.findById(request.getOrderId())
			.orElseThrow(() -> new OrderException(OrderExceptionCode.ORDER_NOT_FOUND));

		if (!OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
			throw new OrderException(ReviewExceptionCode.DELIVERY_NOT_COMPLETE);
		}

		if (!userDetails.getUser().getId().equals(order.getUser().getId())) {
			throw new ReviewException(ReviewExceptionCode.NOT_OWNER_OF_ORDER);
		}

		Review review = Review.builder()
			.content(request.getContent())
			.rating(request.getRating())
			.user(userDetails.getUser())
			.order(order)
			.store(order.getStore())
			.build();

		Review saved = reviewRepository.save(review);

		// 이미지 업로드
		if (request.getFiles() != null) {
			try {
				List<ReviewImage> reviewImages = fileService.uploadAndCreateEntities(
					request.getFiles(),
					"review-images",
					url -> ReviewImage.builder()
						.imgUrl(url)
						.review(saved)
						.build()
				);

				reviewImageRepository.saveAll(reviewImages);
			} catch (Exception e) {
				log.warn("리뷰 이미지 업로드 실패. reviewId = {}, 이유 = {}", saved.getId(), e.getMessage());
			}
		}

		ReviewCreateResponse createdReview = ReviewCreateResponse.builder().reviewId(saved.getId()).build();

		return CommonResponse.of(ReviewSuccessCode.REVIEW_CREATE_SUCCESS, createdReview);
	}

	public CommonResponses<ReviewFindResponse> getReviewsByStoreId(Long storeId, Pageable pageable, Integer minRating,
		Integer maxRating) {

		Page<Review> page = reviewRepository.findByStoreIdAndRatingBetween(storeId, minRating, maxRating, pageable);

		List<Review> reviews = page.getContent();

		List<Long> reviewIds = reviews.stream().map(Review::getId).toList();

		List<OwnerComment> ownerComments = ownerCommentRepository.findAllByReviewIdIn(reviewIds);
		Map<Long, OwnerComment> commentMap = ownerComments.stream()
			.collect(Collectors.toMap(c -> c.getReview().getId(), c -> c));

		List<ReviewFindResponse> responses = new ArrayList<>();
		for (Review review : reviews) {
			OwnerComment ownerComment = commentMap.get(review.getId());
			responses.add(ReviewFindResponse.from(review, ownerComment, review.getReviewImages()));
		}

		Page<ReviewFindResponse> responsePage = new PageImpl<>(responses, pageable, page.getTotalElements());

		return CommonResponses.of(ReviewSuccessCode.REVIEW_FOUND_SUCCESS, responsePage);
	}

	@PreAuthorize("hasRole('USER')")
	public void deleteReviewsById(ReviewDeleteRequestDto dto, UserDetailsImpl userDetails) {

		Review review = reviewRepository.findByIdWithReviewImages(dto.getReviewId())
			.orElseThrow(() -> new ReviewException(ReviewExceptionCode.REVIEW_NOT_FOUND));

		review.validateOwner(userDetails.getUser());

		ownerCommentRepository.findByReviewId(dto.getReviewId()).ifPresent(ownerCommentRepository::delete);

		List<ReviewImage> reviewImages = review.getReviewImages();
		fileService.deleteS3Images(reviewImages, ReviewImage::getImgUrl);

		reviewRepository.delete(review);
	}
}
