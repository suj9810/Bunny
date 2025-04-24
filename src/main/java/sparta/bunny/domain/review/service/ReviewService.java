package sparta.bunny.domain.review.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import sparta.bunny.domain.review.code.ReviewExceptionCode;
import sparta.bunny.domain.review.code.ReviewSuccessCode;
import sparta.bunny.domain.review.dto.request.ReviewCreateRequest;
import sparta.bunny.domain.review.dto.request.ReviewDeleteRequestDto;
import sparta.bunny.domain.review.dto.response.ReviewCreateResponse;
import sparta.bunny.domain.review.dto.response.ReviewFindResponse;
import sparta.bunny.domain.review.entity.Image;
import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.exception.ReviewException;
import sparta.bunny.domain.review.repository.ImageRepository;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
	private final ReviewRepository reviewRepository;
	private final OwnerCommentRepository ownerCommentRepository;
	private final S3Uploader s3Uploader;
	private final ImageRepository imageRepository;

	@Transactional
	public CommonResponse<ReviewCreateResponse> saveReview(ReviewCreateRequest request) throws IOException {

		Review review = Review.builder().content(request.getContent()).rating(request.getRating()).build();

		Review saved = reviewRepository.save(review);

		if (request.getFiles() != null && !request.getFiles().isEmpty()) {
			for (MultipartFile file : request.getFiles()) {
				validateImageExtension(file);
				String url = s3Uploader.upload(file, "images");

				Image image = Image.builder().imgUrl(url).review(saved).build();

				imageRepository.save(image);
			}
		}

		ReviewCreateResponse createdReview = ReviewCreateResponse.builder().reviewId(saved.getId()).build();

		return CommonResponse.of(ReviewSuccessCode.REVIEW_CREATE_SUCCESS, createdReview);
	}

	public CommonResponses<ReviewFindResponse> getReviewsByStoreId(Long storeId, Pageable pageable, Integer minRating,
		Integer maxRating) {
		Page<Review> page = reviewRepository.findByStoresIdAndRatingBetween(storeId, minRating, maxRating, pageable);

		List<Review> reviews = page.getContent();

		List<ReviewFindResponse> responses = new ArrayList<>();
		for (Review review : reviews) {
			OwnerComment ownerComment = ownerCommentRepository.findByReviewId(review.getId()).orElse(null);
			responses.add(ReviewFindResponse.from(review, ownerComment, review.getImages()));
		}

		Page<ReviewFindResponse> responsePage = new PageImpl<>(responses, pageable, page.getTotalElements());

		for (ReviewFindResponse reviewFindResponse : responsePage) {
			System.out.println("reviewFindResponse = " + reviewFindResponse.getImageUrls());
		}

		return CommonResponses.of(ReviewSuccessCode.REVIEW_FOUND_SUCCESS, responsePage);
	}

	public CommonResponse<String> deleteReviewsById(ReviewDeleteRequestDto dto) {

		Review review = reviewRepository.findById(dto.getReviewId())
			.orElseThrow(() -> new ReviewException(ReviewExceptionCode.REVIEW_NOT_FOUND));

		ownerCommentRepository.findByReviewId(dto.getReviewId()).ifPresent(ownerCommentRepository::delete);

		List<Image> images = imageRepository.findAllByReviewId(dto.getReviewId());
		for (Image image : images) {
			String imageUrl = image.getImgUrl();
			s3Uploader.delete(imageUrl); // S3에서 삭제
		}

		imageRepository.deleteAll(images); // DB에서 삭제

		reviewRepository.delete(review);
		return null;
	}

	// 파일 확장자, 크기 검사
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
