package site.mylittlestore.repository.order;

import site.mylittlestore.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryQueryDsl {
    Optional<Order> findNotDeletedAndPaidByIdAndStoreId(Long id, Long storeId);
    List<Order> findAllNotDeletedAndPaidByStoreId(Long storeId);
    Optional<Order> findNotDeletedAndPaidWithStoreById(Long orderId);
}
