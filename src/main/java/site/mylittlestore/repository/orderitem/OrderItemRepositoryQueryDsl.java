package site.mylittlestore.repository.orderitem;

import site.mylittlestore.domain.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepositoryQueryDsl {
    Optional<OrderItem> findOrderedById(Long id);
    Optional<OrderItem> findByIdWithItem(Long id);
    Optional<OrderItem> findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);

    Optional<OrderItem> findOrderItemByOrderIdAndItemIdAndPrice(Long orderId, Long itemId, int price);

    List<OrderItem> findAllOrderItemByOrderIdOrderByTime(Long orderId);
    List<Long> findAllOrderItemIdByOrderId(Long orderId);
    void deleteByChangingStatus(Long id);
}
