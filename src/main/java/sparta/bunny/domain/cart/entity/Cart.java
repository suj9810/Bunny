package sparta.bunny.domain.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import sparta.bunny.domain.cart.dto.CartMenuItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@RedisHash("carts")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long userId;

    private Long storeId;

    private List<CartMenuItem> menus = new ArrayList<>();

    //장바구니 유효 시간 24시간
    @TimeToLive
    private Long ttl = 86400L;

    public Cart(Long userId, Long storeId, List<CartMenuItem> menus) {
        this.userId = userId;
        this.storeId = storeId;
        this.menus = menus;
    }
}