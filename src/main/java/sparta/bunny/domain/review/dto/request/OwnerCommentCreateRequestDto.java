package sparta.bunny.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerCommentCreateRequestDto {

	@NotBlank
	private Long reviewId;
	@NotBlank
	private String content;
}
