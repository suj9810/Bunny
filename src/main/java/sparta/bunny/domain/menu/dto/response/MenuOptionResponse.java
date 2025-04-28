package sparta.bunny.domain.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.menu.entity.MenuOption;

/**
 * 메뉴 옵션 응답
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuOptionResponse {
	private String name;
	private Integer price;

	/**
	 * Of menu option response.
	 *
	 * @param option the option
	 * @return the menu option response
	 */
	public static MenuOptionResponse of(MenuOption option) {
		return MenuOptionResponse.builder()
			.name(option.getName())
			.price(option.getPrice())
			.build();
	}
}