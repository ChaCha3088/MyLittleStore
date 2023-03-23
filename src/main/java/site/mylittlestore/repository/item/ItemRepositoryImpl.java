package site.mylittlestore.repository.item;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.dto.item.ItemCreationDto;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.item.QItemFindDto;
import site.mylittlestore.enumstorage.status.ItemStatus;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static site.mylittlestore.domain.item.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryQueryDsl {
    private final EntityManager em;

    @Override
    public Optional<ItemFindDto> findItemDtoById(Long id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(
                queryFactory
                        .select(new QItemFindDto(
                                item.id,
                                item.store.id,
                                item.name,
                                item.price,
                                item.stock,
                                item.image
                        ))
                        .from(item)
                        .where(item.id.eq(id), item.itemStatus.eq(ItemStatus.ONSALE))
                        .fetchOne()
        );
    }

    @Override
    public List<ItemFindDto> findAllItemDtoByStoreId(Long storeId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .select(new QItemFindDto(
                        item.id,
                        item.store.id,
                        item.name,
                        item.price,
                        item.stock,
                        item.image
                ))
                .from(item)
                .where(item.store.id.eq(storeId), item.itemStatus.eq(ItemStatus.ONSALE))
                .orderBy(item.id.asc())
                .fetch();
    }

    @Override
    public void deleteById(Long id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        queryFactory
                .update(item)
                .where(item.id.eq(id))
                .set(item.itemStatus, ItemStatus.DELETED)
                .execute();
    }
}
