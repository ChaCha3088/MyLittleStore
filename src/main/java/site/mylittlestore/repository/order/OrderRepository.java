package site.mylittlestore.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryQueryDsl {
    Optional<Order> findNotDeletedAndPaidByIdAndStoreId(Long id, Long storeId);
    Optional<Order> findNotDeletedAndPaidWithStoreById(Long orderId);
    Optional<Order> findNotDeletedAndPaidWithStoreAndOrderItemsById(Long orderId);
    List<Order> findAllNotDeletedAndPaidByStoreId(Long storeId);
}
