package sparta.bunny.domain.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.menu.entity.MenuOption;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionResponse {

	private String name;
	private Integer price;

	public static MenuOptionResponse of(MenuOption option) {
		return MenuOptionResponse.builder()
			.name(option.getName())
			.price(option.getPrice())
			.build();
	}
}