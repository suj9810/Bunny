package sparta.bunny.domain.order.dto;

import lombok.Getter;

@Getter
public class ChangeOrderStatusRequestDto {

    private final String orderStatus;

    public ChangeOrderStatusRequestDto(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
