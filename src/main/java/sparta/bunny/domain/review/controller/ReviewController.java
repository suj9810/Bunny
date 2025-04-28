package sparta.bunny.domain.review.controller;

import java.io.IOException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.review.dto.request.ReviewCreateRequest;
import sparta.bunny.domain.review.dto.request.ReviewDeleteRequestDto;
import sparta.bunny.domain.review.dto.response.ReviewCreateResponse;
import sparta.bunny.domain.review.dto.response.ReviewFindResponse;
import sparta.bunny.domain.review.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;
	private final ObjectMapper objectMapper;

	/**
	 * 이미지와 함께 리뷰 저장 from-data로 할것
	 * @param dto 이미지, 리뷰
	 * @return 생성된 리뷰 정보
	 * @throws IOException
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<ReviewCreateResponse>> createReviewWithImages(
		@Valid @ModelAttribute ReviewCreateRequest dto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) throws IOException {
		CommonResponse<ReviewCreateResponse> response = reviewService.saveReview(dto, userDetails);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 리뷰 조회
	 * @param storeId 상점 id
	 * @param minRating 최소 별점
	 * @param maxRating 최대 별점
	 * @param page 현재 페이지
	 * @param size 사이즈
	 * @return 조회된 리뷰
	 */
	@GetMapping
	public ResponseEntity<CommonResponses<ReviewFindResponse>> findReviewByStoreId(
		@RequestParam(value = "storeId") Long storeId,
		@RequestParam(value = "minRating", defaultValue = "1", required = false) Integer minRating,
		@RequestParam(value = "maxRating", defaultValue = "5", required = false) Integer maxRating,
		@RequestParam(value = "page", defaultValue = "0", required = false) int page,
		@RequestParam(value = "size", defaultValue = "10", required = false) int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		CommonResponses<ReviewFindResponse> reviews = reviewService.getReviewsByStoreId(storeId, pageable, minRating,
			maxRating);
		return ResponseEntity.status(HttpStatus.OK).body(reviews);
	}

	/**
	 * 리뷰 삭제
	 * @param dto 삭제할 리뷰 id
	 * @return 삭제 성공 여부
	 */
	@DeleteMapping
	public ResponseEntity<String> deleteReview(
		@Valid @RequestBody ReviewDeleteRequestDto dto,
		@AuthenticationPrincipal UserDetailsImpl userDetails

	) {
		reviewService.deleteReviewsById(dto, userDetails);
		return ResponseEntity.status(HttpStatus.OK).body("리뷰 삭제에 성공하였습니다.");
	}
}
