package sparta.bunny.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderMenuDto {
    private Long menuId;
    private String menuName;
    private int quantity;
    private int price;
}
