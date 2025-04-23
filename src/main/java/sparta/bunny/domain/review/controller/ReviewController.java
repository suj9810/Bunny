package sparta.bunny.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.review.dto.request.ReviewCreateRequest;
import sparta.bunny.domain.review.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<CommonResponse> saveReview(@Valid @RequestBody ReviewCreateRequest request) {
		CommonResponse commonResponse = reviewService.saveReview(request.getContent());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commonResponse);
	}
}
