package sparta.bunny.order.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
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
