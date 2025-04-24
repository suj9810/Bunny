package sparta.bunny.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.bunny.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
