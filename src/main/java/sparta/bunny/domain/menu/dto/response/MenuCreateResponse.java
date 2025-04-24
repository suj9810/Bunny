package sparta.bunny.domain.menu.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
// @NoArgsConstructor
@AllArgsConstructor
public class MenuCreateResponse {

	private final Long menuId;
	private final String name;
	private final String description;
	private final String imageUrl;
	private final Integer price;
	private final String status;
	private final StoreInfoResponse store;
	private final List<MenuOptionResponse> options;

}
