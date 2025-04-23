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

@Getter
@Entity
@Table(name = "orderMenus")
@NoArgsConstructor
public class OrderMenu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderMenuId;

	@Column
	private int orderCnt;

	// 메뉴
	@ManyToOne
	@JoinColumn(name = "menu_id")
	private Menus menus;

	// 주문
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
}
