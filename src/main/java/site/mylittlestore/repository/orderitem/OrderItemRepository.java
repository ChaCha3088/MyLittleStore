package site.mylittlestore.repository.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryQueryDsl {
//    Optional<OrderItem> findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);
    Optional<OrderItem> findByIdWithItem(Long id);

    List<OrderItem> findAllOrderItemByOrderIdOrderByTime(Long orderId);

    List<Long> findAllOrderItemIdByOrderId(Long orderId);

    Optional<OrderItem> findOrderItemByOrderIdAndItemIdAndPrice(Long orderId, Long itemId, int price);

    Optional<OrderItem> findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);
}
