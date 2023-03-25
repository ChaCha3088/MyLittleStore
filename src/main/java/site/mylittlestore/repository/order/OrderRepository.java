package site.mylittlestore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryQueryDsl {

    List<Order> findAllByStoreId(Long storeId);
    Optional<Order> findOrderAndOrderItemsByIdOrderByTime(Long orderId);
}
