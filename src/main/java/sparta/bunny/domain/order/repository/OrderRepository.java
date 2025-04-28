package sparta.bunny.domain.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import sparta.bunny.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@Query("SELECT o FROM Order o JOIN FETCH o.orderMenus om JOIN FETCH om.menu WHERE o.id = :orderId")
	Optional<Order> findByIdWithOrderMenus(@Param("orderId") Long orderId);

}
