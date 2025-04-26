package sparta.bunny.domain.cart.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartMenu implements Serializable {

	private static final long serialVersionUID = 1L;

	//담을 메뉴 ID
	private Long menuId;

	private String menuName;

	private Integer menuPrice;

	//수량
	@Setter
	private int quantity;
}
