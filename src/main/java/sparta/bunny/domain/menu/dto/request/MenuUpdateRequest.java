package sparta.bunny.domain.menu.dto.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuUpdateRequest {
	private String name;
	private String description;
	private Integer price;
	private String imageUrl;
	private List<MenuOptionRequest> options = new ArrayList<>();
}
