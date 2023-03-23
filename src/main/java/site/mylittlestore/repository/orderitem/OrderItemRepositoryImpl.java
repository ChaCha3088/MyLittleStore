package site.mylittlestore.repository.orderitem;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.domain.OrderItem;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static site.mylittlestore.domain.QOrderItem.orderItem;
import static site.mylittlestore.domain.item.QItem.item;

@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepositoryQueryDsl {
    private final EntityManager em;
    @Override
    public Optional<OrderItem> findByIdWithItem(Long id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(queryFactory
                        .select(orderItem)
                        .from(orderItem)
                        .join(orderItem.item, item).fetchJoin()
                        .where(orderItem.id.eq(id))
                        .fetchOne());
    }


    @Override
    public List<OrderItem> findAllOrderItemByOrderIdOrderByTime(Long orderId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                        .select(orderItem)
                        .from(orderItem)
                        .join(orderItem.item, item).fetchJoin()
                        .where(orderItem.order.id.eq(orderId))
                        .orderBy(orderItem.time.asc())
                        .fetch();
    }

    @Override
    public List<Long> findAllOrderItemIdByOrderId(Long orderId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                        .select(orderItem.id)
                        .from(orderItem)
                        .where(orderItem.order.id.eq(orderId))
                        .fetch();
    }
}
