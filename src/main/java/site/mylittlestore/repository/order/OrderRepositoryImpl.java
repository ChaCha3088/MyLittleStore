package site.mylittlestore.repository.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.domain.Order;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static site.mylittlestore.domain.QOrder.order;
import static site.mylittlestore.domain.QOrderItem.orderItem;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryQueryDsl {

    private final EntityManager em;

    @Override
    public Optional<Order> findOrderAndOrderItemsByIdOrderByTime(Long orderId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(queryFactory
                .select(order)
                .from(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .where(order.id.eq(orderId))
                .orderBy(orderItem.time.asc())
                .fetchOne());
    }

    @Override
    public List<Order> findAllOrderByStoreIdOrderByOrderNumber(Long storeId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .select(order)
                .distinct()
                .from(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .where(order.store.id.eq(storeId))
                .orderBy(order.createdDate.asc())
                .fetch();
    }
}
