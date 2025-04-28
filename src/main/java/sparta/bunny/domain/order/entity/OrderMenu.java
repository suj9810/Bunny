package sparta.bunny.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sparta.bunny.domain.menu.entity.Menu;

@Getter
@Entity
@Table(name = "orderMenus")
@NoArgsConstructor
public class OrderMenu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 주문
	@Setter
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	// 메뉴
	@ManyToOne
	@JoinColumn(name = "menu_id")
	private Menu menu;

	private int orderCnt;

	private int totalPrice;

	public OrderMenu(Menu menu, int quantity) {
		this.menu = menu;
		this.orderCnt = quantity;
		this.totalPrice = menu.getPrice() * quantity;
	}

}
