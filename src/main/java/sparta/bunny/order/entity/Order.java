package sparta.bunny.order.entity;

import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.Getter;
import sparta.bunny.order.enums.OrderStatus;

import java.awt.*;

@Getter
@Entity
@Table(name = "order")
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
