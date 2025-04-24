package sparta.bunny.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 전체 구조를 표현하는 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Serializable {
    // 어떤 가게의 장바구니인지
    private Long storeId;
    // 어떤 메뉴들이 들어있는지
    private List<CartMenuItem> items = new ArrayList<>();

    public CartDto(Long storeId) {
        this.storeId = storeId;
    }
}

