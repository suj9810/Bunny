package sparta.bunny.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.entity.ReviewImage;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewFindResponse {
	private long reviewId;
	private long orderId;
	private int rating;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private String ownerComment;
	private List<String> imageUrls;

	public static ReviewFindResponse from(Review review, OwnerComment ownerComment, List<ReviewImage> reviewImages) {
		return ReviewFindResponse.builder()
			.reviewId(review.getId())
			.orderId(review.getOrder().getId())
			.rating(review.getRating())
			.content(review.getContent())
			.createdAt(review.getCreatedAt())
			.modifiedAt(review.getModifiedAt())
			.ownerComment(ownerComment != null ? ownerComment.getContent() : null)
			.imageUrls(reviewImages == null || reviewImages.isEmpty() ? null :
				reviewImages.stream().map(ReviewImage::getImgUrl).toList())
			.build();
	}

}
