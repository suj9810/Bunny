package sparta.bunny.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.common.audit.BaseEntity;
import sparta.bunny.domain.menu.entity.Menu;

@Getter
@Entity
@Table(name = "orderMenus")
@NoArgsConstructor
public class OrderMenu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 주문
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	// 메뉴
	@ManyToOne
	@JoinColumn(name = "menu_id")
	private Menu menus;

	private int orderCnt;

	private int totalPrice;

}
