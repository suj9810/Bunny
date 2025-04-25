package sparta.bunny.domain.order.dto;

import lombok.Getter;
import sparta.bunny.domain.menu.entity.Menu;

@Getter
public class OrderRequestDto {

    private final int orderCnt;

    private final Menu menu;

    private final String orderStatus;

    public OrderRequestDto(int orderCnt, Menu menu, String orderStatus) {
        this.orderCnt = orderCnt;
        this.menu = menu;
        this.orderStatus = orderStatus;
    }
}
