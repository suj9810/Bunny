package sparta.bunny.domain.review.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.S3.S3Uploader;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.order.entity.Order;
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
import sparta.bunny.domain.review.repository.ImageRepository;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;
import sparta.bunny.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

	private final ReviewRepository reviewRepository;
	private final OwnerCommentRepository ownerCommentRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	// S3
	private final S3Uploader s3Uploader;
	private final ImageRepository imageRepository;

	@Transactional
	public CommonResponse<ReviewCreateResponse> saveReview(
		ReviewCreateRequest request, UserDetailsImpl userDetails
	) throws IOException {

		Order order = orderRepository.findById(request.getOrderId())
			.orElseThrow(() -> new RuntimeException("주문 정보가 일치하지 않습니다.")); // Todo - Order Exception 사용하기

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

		// Todo - 서비스 분리
		if (request.getFiles() != null && !request.getFiles().isEmpty()) {
			for (MultipartFile file : request.getFiles()) {
				validateImageExtension(file);
				String url = s3Uploader.upload(file, "images");

				ReviewImage reviewImage = ReviewImage.builder().imgUrl(url).review(saved).build();

				imageRepository.save(reviewImage);
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

	public void deleteReviewsById(ReviewDeleteRequestDto dto, UserDetailsImpl userDetails) {

		Review review = reviewRepository.findById(dto.getReviewId())
			.orElseThrow(() -> new ReviewException(ReviewExceptionCode.REVIEW_NOT_FOUND));

		review.validateOwner(userDetails.getUser());

		ownerCommentRepository.findByReviewId(dto.getReviewId()).ifPresent(ownerCommentRepository::delete);

		List<ReviewImage> reviewImages = imageRepository.findAllByReviewId(dto.getReviewId());
		for (ReviewImage reviewImage : reviewImages) {
			String imageUrl = reviewImage.getImgUrl();
			s3Uploader.delete(imageUrl); // S3에서 삭제
		}

		reviewRepository.delete(review);
	}

	/**
	 * 파일 확장자, 크기 검사
	 * @param file
	 */
	private void validateImageExtension(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();

		// 파일 크기 검사
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 5MB까지 업로드할 수 있습니다.");
		}

		// 파일 확장자 검사
		if (originalFilename == null || !(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")
			|| originalFilename.endsWith(".png"))) {
			throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다.");
		}
	}

}
