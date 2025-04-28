package sparta.bunny.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerCommentCreateRequestDto {

	@NotNull
	private Long reviewId;
	@NotBlank
	private String content;
}
