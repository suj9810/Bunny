package sparta.bunny.domain.cart.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class CartMenuRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	//담을 메뉴 ID
	private Long menuId;

	//수량
	@Setter
	private int quantity;
}
