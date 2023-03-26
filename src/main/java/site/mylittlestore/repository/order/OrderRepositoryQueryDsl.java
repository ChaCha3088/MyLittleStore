package site.mylittlestore.repository.order;

import site.mylittlestore.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryQueryDsl {
    Optional<Order> findOrderWithOrderItemsByIdOrderByTime(Long orderId);
    Optional<Order> findOrderWithStoreAndOrderItemsByIdOrderByTime(Long orderId);


    //가게에 속한 테이블만 찾아야지.
    List<Order> findAllOrderByStoreIdOrderByOrderNumber(Long storeId);

}
