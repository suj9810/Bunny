package sparta.bunny.domain.menu.dto.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuCreateRequest {

	@NotNull(message = "스토어 ID는 필수입니다.")
	private Long storeId;

	@NotEmpty(message = "메뉴 이름은 필수입나다.")
	private String name;

	@Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.")
	private String description;

	@NotNull(message = "가격은 필수입니다.")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private Integer price;

	@Size(max = 1024, message = "이미지 URL은 최대 1024자까지 가능합니다.")
	private String imageUrl;

	private List<MenuOptionRequest> options = new ArrayList<>();
}
