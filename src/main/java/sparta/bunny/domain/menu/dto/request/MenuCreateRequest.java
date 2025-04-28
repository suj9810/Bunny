package sparta.bunny.domain.menu.dto.request;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 메뉴 생성 요청
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuCreateRequest {

	@NotNull(message = "스토어 ID는 필수입니다.")
	private Long storeId;

	@NotBlank(message = "메뉴 이름은 필수입나다.")
	private String name;

	@Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.")
	private String description;

	@NotNull(message = "가격은 필수입니다.")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private Integer price;

	private List<MultipartFile> files;

	@Builder.Default
	private List<MenuOptionRequest> options = new ArrayList<>();
}
