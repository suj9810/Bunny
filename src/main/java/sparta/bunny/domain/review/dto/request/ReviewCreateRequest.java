package sparta.bunny.domain.review.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

	@NotBlank
	private String content;

	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;

	@NotNull
	private Long orderId;

	private List<MultipartFile> files;
}
