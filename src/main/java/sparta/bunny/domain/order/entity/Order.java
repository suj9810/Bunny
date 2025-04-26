package sparta.bunny.domain.order.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sparta.bunny.common.audit.BaseEntity;
import sparta.bunny.domain.order.enums.OrderStatus;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.user.entity.User;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 유저 ManyToOne
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	// 가게 ManyToOne
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;

	private LocalDateTime orderedAt = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.PENDING;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderMenu> orderMenus = new ArrayList<>();

	public Order(User user, Store store) {
		this.user = user;
		this.store = store;
	}

	public void addMenu(OrderMenu menu) {
		this.orderMenus.add(menu);
		menu.setOrder(this); // FK 설정!
	}

	public void updateOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

}
