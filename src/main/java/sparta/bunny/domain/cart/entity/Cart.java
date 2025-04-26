package sparta.bunny.domain.cart.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@RedisHash("carts")
public class Cart implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long userId;

	private Long storeId;

	private List<CartMenu> menus = new ArrayList<>();

	// 장바구니 유효 시간 24시간
	@TimeToLive
	private Long ttl = 86400L;

	public Cart(Long userId, Long storeId, List<CartMenu> menus) {
		this.userId = userId;
		this.storeId = storeId;
		this.menus = menus;
	}

	public Cart(Long storeId) {
		this.storeId = storeId;
	}

	public Cart(Long storeId, List<CartMenu> menus) {
		this.storeId = storeId;
		this.menus = menus;
	}
}
