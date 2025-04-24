package sparta.bunny.domain.menu.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuOptionRequest {
	private String name;
	private Integer price;
}
