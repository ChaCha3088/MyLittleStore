package site.mylittlestore.repository.storetable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.repository.order.OrderRepositoryQueryDsl;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static site.mylittlestore.domain.QOrder.order;
import static site.mylittlestore.domain.QOrderItem.orderItem;
import static site.mylittlestore.domain.QStore.store;
import static site.mylittlestore.domain.QStoreTable.storeTable;

@RequiredArgsConstructor
public class StoreTableRepositoryImpl implements StoreTableRepositoryQueryDsl {

    private final EntityManager em;

//    @Override
//    public Optional<Order> findOrderAndOrderItemsByIdOrderByTime(Long orderId) {
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//
//        return Optional.ofNullable(queryFactory
//                .select(order)
//                .from(order)
//                .join(order.orderItems, orderItem).fetchJoin()
//                .where(order.id.eq(orderId))
//                .orderBy(orderItem.time.asc())
//                .fetchOne());
//    }

    @Override
    public Optional<StoreTable> findStoreTableWithStoreByIdAndStoreId(Long id, Long storeId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(queryFactory
                .select(storeTable)
                .from(storeTable)
                .join(storeTable.store, store).fetchJoin()
//                        .join(storeTable.order, order).fetchJoin()
                .where(storeTable.id.eq(id).and(storeTable.store.id.eq(storeId)))
                .fetchOne());
    }

    @Override
    public Optional<StoreTable> findStoreTableWithStoreAndOrderByIdAndStoreId(Long id, Long storeId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(queryFactory
                .select(storeTable)
                .from(storeTable)
                .join(storeTable.store, store).fetchJoin()
                .join(storeTable.order, order).fetchJoin()
                .where(storeTable.id.eq(id).and(storeTable.store.id.eq(storeId)))
                .fetchOne());
    }
    @Override
    public List<StoreTable> findAllStoreTableWithOrderByStoreId(Long storeId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .select(storeTable)
//                .distinct()
                .from(storeTable)
                .join(storeTable.order, order).fetchJoin()
                .where(storeTable.store.id.eq(storeId))
                .orderBy(storeTable.id.asc())
                .fetch();
    }
}
