package sparta.bunny.domain.menu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *	메뉴 옵션 요청
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionRequest {
	private String name;
	private Integer price;
}
