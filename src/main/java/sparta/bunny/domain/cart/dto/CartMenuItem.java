package sparta.bunny.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class CartMenuItem implements Serializable {

    private static final long serialVersionUID = 1L;

    //담을 메뉴 ID
    private Long menuId;
    //메뉴이름
    private String menuName;
    //가격
    private int price;
    //수량
    @Setter
    private int quantity;
}
