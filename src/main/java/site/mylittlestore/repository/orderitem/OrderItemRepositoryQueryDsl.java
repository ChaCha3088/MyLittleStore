package site.mylittlestore.repository.orderitem;

import site.mylittlestore.domain.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepositoryQueryDsl {
    Optional<OrderItem> findById(Long id);
    Optional<OrderItem> findWithItemById(Long id);
    Optional<OrderItem> findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);
    Optional<OrderItem> findOrderItemByOrderIdAndItemIdAndPrice(Long orderId, Long itemId, int price);
    List<OrderItem> findAllByOrderId(Long orderId);

    List<OrderItem> findAllOrderItemByOrderIdOrderByTime(Long orderId);
    List<OrderItem> findAllOrderItemIdByOrderId(Long orderId);
    Optional<OrderItem> findByOrderIdAndItemIdAndPrice(Long orderId, Long itemId, int price);
    Optional<OrderItem> findByOrderIdAndOrderItemIdAndItemIdAndPrice(Long orderId, Long orderItemId, Long itemId, int price);
    void deleteByChangingStatus(Long id);
}
