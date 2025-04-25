package sparta.bunny.domain.menu.dto.response;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// @NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

	private final Long menuId;

	@NotEmpty(message = "메뉴 이름은 필수입나다.")
	private final String name;

	@Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다.")
	private final String description;

	@NotNull(message = "가격은 필수입니다.")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다.")
	private final Integer price;

	@Size(max = 1024, message = "이미지 URL은 최대 1024자까지 가능합니다.")
	private final String imageUrl;

	private final String status;
	private final StoreInfoResponse store;
	private final List<MenuOptionResponse> options;

}
