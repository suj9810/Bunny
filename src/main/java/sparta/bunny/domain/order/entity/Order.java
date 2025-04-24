package sparta.bunny.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.common.audit.BaseEntity;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.stores.entity.Stores;
import sparta.bunny.domain.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 유저 ManyToOne
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	// 가게 ManyToOne
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Stores store;

	private LocalDateTime orderedAt = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.PENDING;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderMenu> orderMenus = new ArrayList<>();

}
