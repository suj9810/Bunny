package sparta.bunny.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.order.enums.OrderStatus;

@Getter
@Entity
@Table(name = "order")
@NoArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@Column
	private double totalPrice;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	// 가게 ManyToOne
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;

	// 유저 ManyToOne
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}
