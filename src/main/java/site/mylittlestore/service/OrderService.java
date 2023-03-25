package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.StoreTable;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemUpdateDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemDto;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;
import site.mylittlestore.enumstorage.errormessage.*;
import site.mylittlestore.enumstorage.status.OrderStatus;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.dto.orderitem.OrderItemCreationDto;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.item.NotEnoughStockException;
import site.mylittlestore.exception.orderitem.NoSuchOrderItemException;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Order> findOrderById = orderRepository.findById(orderId);

        //주문이 없으면 예외 발생
        //Dto로 변환
        return findOrderById.orElseThrow(()
                -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()))
                .toOrderDtoWithOrderItemId();
    }

    public OrderDtoWithOrderItemDto findOrderDtoById(Long orderId) throws NoSuchOrderException {
        Optional<Order> findOrderById = orderRepository.findOrderAndOrderItemsByIdOrderByTime(orderId);

        findOrderById = findOrderById.or(() -> orderRepository.findById(orderId));

        //테이블이 없으면 예외 발생
        //Dto로 변환
        return findOrderById.orElseThrow(()
                -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()))
                .toOrderDtoWithOrderItemDto();
    }

    public List<OrderItemFindDto> findAllOrderItemByOrderId(Long orderId) {
        //테이블에 속한 주문 상품만 찾아야지.
        List<OrderItem> findOrderItemByOrderId = orderItemRepository.findAllOrderItemByOrderIdOrderByTime(orderId);

        //Dto로 변환
        return findOrderItemByOrderId.stream()
                .map(m -> m.toOrderItemDto())
                .collect(Collectors.toList());
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

    @Transactional
    public Long createOrderItem(OrderItemFindDto orderItemFindDto) throws NoSuchStoreException, StoreClosedException, NoSuchOrderException, NotEnoughStockException {
        Store findStoreByStoreId = findStoreByStoreId(orderItemFindDto.getStoreId());

        //가게가 열린 상태인지 확인
        if (findStoreByStoreId.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_IS_CLOSED.getMessage());
        }

        try {
            //테이블에 상품 Id와 상품 가격이 같은 주문이 존재하는지 확인
            OrderItem findOrderItemWithItemId = validateOrderItemExistenceWithItemIdAndPrice(orderItemFindDto.getOrderId(), orderItemFindDto.getItemId(), orderItemFindDto.getPrice());

            //테이블에 상품이 이미 있다면,
            //테이블에 있는 해당 상품의 수량을 늘려주고, 상품의 재고를 낮춰준다.
            Item item = findOrderItemWithItemId.addCount(orderItemFindDto.getCount());

            //저장
            itemRepository.save(item);
            OrderItem savedOrderItem = orderItemRepository.save(findOrderItemWithItemId);

            return savedOrderItem.getId();

        } catch (NoSuchOrderItemException e) {
            //테이블에 상품이 없으면,

            //테이블을 찾는다.
            Order findOrder = findById(orderItemFindDto.getOrderId());

            //상품을 찾는다.
            Item findItem = findItemById(orderItemFindDto.getItemId());

            //새로운 상품 추가
            OrderItem createdOrderItem = findOrder.createOrderItem(OrderItemCreationDto.builder()
                    .orderId(findOrder.getId())
                    .item(findItem)
                    .price(orderItemFindDto.getPrice())
                    .count(orderItemFindDto.getCount())
                    .build());

            //저장
            itemRepository.save(findItem);
            OrderItem savedOrderItem = orderItemRepository.save(createdOrderItem);

            return savedOrderItem.getId();
        }
    }

    @Transactional
    public Long updateOrderItem(OrderItemUpdateDto orderItemUpdateDto) throws NoSuchStoreException, StoreClosedException, NoSuchOrderItemException {
        Store findStoreByStoreId = findStoreByStoreId(orderItemUpdateDto.getStoreId());

        //가게가 열린 상태인지 확인
        if (findStoreByStoreId.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_IS_CLOSED.getMessage());
        }

        //테이블에 상품 Id이 같은 주문이 존재하는지 확인
        OrderItem findOrderItemWithOrderIdAndItemId = orderItemRepository.findOrderItemByOrderIdAndItemId(orderItemUpdateDto.getOrderId(), orderItemUpdateDto.getItemId())
                .orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));

        Optional<Integer> price = Optional.ofNullable(orderItemUpdateDto.getPrice());
        Optional<Integer> count = Optional.ofNullable(orderItemUpdateDto.getCount());

        price.ifPresent(findOrderItemWithOrderIdAndItemId::updatePrice);
        count.ifPresent(findOrderItemWithOrderIdAndItemId::updateCount);

        //저장
        OrderItem savedOrderItem = orderItemRepository.save(findOrderItemWithOrderIdAndItemId);

        return savedOrderItem.getId();
    }

    @Transactional
    public void deleteOrderItem(Long orderItemId) throws EmptyResultDataAccessException {
        //테이블에 상품 Id가 같은 주문이 존재하는지 확인하고 삭제
        OrderItem findOrderItemById = orderItemRepository.findById(orderItemId)
                        .orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));

        try {
            //테이블에 있는 해당 상품의 재고를 늘려주고, 주문을 삭제한다.
            Item item = itemRepository.findById(findOrderItemById.getItem().getId())
                    .orElseThrow(() -> new NoSuchItemException(ItemErrorMessage.NO_SUCH_ITEM.getMessage()));

            item.increaseStock(findOrderItemById.getCount());

            //저장
            itemRepository.save(item);
        } catch (NoSuchItemException e) {
            //상품이 없으면, 주문 상품만 삭제
        } finally {
            orderItemRepository.delete(findOrderItemById);
        }
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

    private OrderItem validateOrderItemExistenceWithItemIdAndPrice(Long orderId, Long itemId, int price) throws NoSuchOrderException, NoSuchOrderItemException {
        //주문 상품에 테이블 Id, 상품 Id, 가격이 같은 상품이 존재하는지 확인
        //해당 조건을 만족하는 상품이 없으면 예외 발생
        return orderItemRepository.findOrderItemByOrderIdAndItemIdAndPrice(orderId, itemId, price)
                .orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));
    }

    private boolean validateDuplicateOrderItemWithItemName(Order order, String newItemName) throws IllegalStateException {
        //테이블 안에 이름이 같은 상품이 있는지 검증
        return order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getItem().getName().equals(newItemName))
                .findFirst()
                .isPresent();
    }

}
