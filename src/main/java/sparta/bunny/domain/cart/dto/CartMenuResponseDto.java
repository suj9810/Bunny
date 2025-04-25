package sparta.bunny.domain.cart.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sparta.bunny.domain.cart.entity.CartMenu;

@Getter
@AllArgsConstructor
public class CartMenuResponseDto implements Serializable {

	// 어떤 가게의 장바구니인지
	private Long storeId;
	// 어떤 메뉴들이 들어있는지
	private List<CartMenu> menus = new ArrayList<>();

	public CartMenuResponseDto(Long storeId) {
		this.storeId = storeId;
	}
}
