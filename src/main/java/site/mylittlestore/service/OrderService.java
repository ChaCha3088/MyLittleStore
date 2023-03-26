package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;
import site.mylittlestore.enumstorage.errormessage.*;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.exception.storetable.NoSuchStoreTableException;
import site.mylittlestore.exception.storetable.OrderAlreadyExistException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.repository.orderitem.OrderItemRepository;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.order.OrderRepository;
import site.mylittlestore.repository.storetable.StoreTableRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final StoreTableRepository storeTableRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderDtoWithOrderItemId findOrderDtoWithOrderItemIdById(Long orderId) throws NoSuchOrderException {
        Optional<Order> findOrderById = orderRepository.findUsingById(orderId);

        //주문이 없으면 예외 발생
        //Dto로 변환
        return findOrderById.orElseThrow(()
                -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()))
                .toOrderDtoWithOrderItemId();
    }

    public OrderDtoWithOrderItemDto findOrderDtoById(Long orderId) throws NoSuchOrderException {
        //주문이 없으면 예외 발생
        Order order = orderRepository.findOrderWithOrderItemsAndItemByIdOrderByTime(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));

        //Dto로 변환
        return order.toOrderDtoWithOrderItemDto();
    }

    @Transactional
    public Long createOrder(Long storeId, Long storeTableId) throws NoSuchStoreException, StoreClosedException {
        Optional<StoreTable> storeTableWithStoreByIdAndStoreId = storeTableRepository.findStoreTableWithStoreByIdAndStoreId(storeTableId, storeId);

        //테이블이 없으면 예외 발생
        StoreTable storeTable = storeTableWithStoreByIdAndStoreId.orElseThrow(()
                -> new NoSuchStoreTableException(StoreTableErrorMessage.NO_SUCH_STORE_TABLE.getMessage()));

        Order order = storeTable.getOrder();
        Store store = storeTable.getStore();

        //테이블에 주문이 이미 존재하는지 확인
        if (order != null) {
            throw new OrderAlreadyExistException(StoreTableErrorMessage.ORDER_ALREADY_EXIST.getMessage());
        }

        //가게가 열린 상태인지 확인
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_IS_CLOSED.getMessage());
        }

        //이제 테이블에 주문이 없다면, 주문 생성
        Order createOrder = Order.builder()
                .store(store)
                .storeTable(storeTable)
                .build();

        //주문 저장
        Order savedOrder = orderRepository.save(createOrder);

        return savedOrder.getId();
    }

    private Store findStoreByStoreId(Long storeId) throws NoSuchStoreException {
        Optional<Store> findStoreById = storeRepository.findById(storeId);

        return findStoreById.orElseThrow(() -> new NoSuchStoreException(StoreErrorMessage.NO_SUCH_STORE.getMessage()));
    }

    private Order findById(Long orderId) throws NoSuchOrderException {
        //테이블을 찾는다.
        Optional<Order> orderById = orderRepository.findById(orderId);

        //테이블이 없으면, 예외 발생
        return orderById.orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));
    }

    private Item findItemById(Long itemId) throws NoSuchItemException {
        //상품을 찾는다.
        Optional<Item> findItemById = itemRepository.findById(itemId);

        //상품이 없으면, 예외 발생
        return findItemById.orElseThrow(() -> new NoSuchItemException(ItemErrorMessage.NO_SUCH_ITEM.getMessage()));
    }

    private boolean validateDuplicateOrderItemWithItemName(Order order, String newItemName) throws IllegalStateException {
        //테이블 안에 이름이 같은 상품이 있는지 검증
        return order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getItem().getName().equals(newItemName))
                .findFirst()
                .isPresent();
    }

}
