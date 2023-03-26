package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.orderitem.OrderItemCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemDtoWithItemName;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.item.NotEnoughStockException;
import site.mylittlestore.exception.orderitem.NoSuchOrderItemException;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.repository.order.OrderRepository;
import site.mylittlestore.repository.orderitem.OrderItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ItemRepository itemRepository;

    public OrderItemFindDto findOrderItemDtoById(Long orderItemId) {
        //주문이 없으면 예외 발생
        //Dto로 변환
        return orderItemRepository.findById(orderItemId).orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage())).toOrderItemDto();
    }

    public OrderItemDtoWithItemFindDto findOrderItemByIdWithItemFindDto(Long orderItemId) throws NoSuchOrderItemException {
        Optional<OrderItem> findOrderItemById = orderItemRepository.findByIdWithItem(orderItemId);

        //주문 상품이 없으면 예외 발생
        //Dto로 변환
        return findOrderItemById.orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage())).toOrderItemDtoWithItemFindDto();
    }

    public List<OrderItemDtoWithItemName> findAllOrderItemDtoWithItemNameByOrderIdOrderByTime(Long orderId) {
        List<OrderItem> findOrderItemByOrderId = orderItemRepository.findAllOrderItemByOrderIdOrderByTime(orderId);

        //Dto로 변환
        return findOrderItemByOrderId.stream().map(OrderItem::toOrderItemDtoWithItemName).collect(Collectors.toList());
    }

    @Transactional
    public Long createOrderItem(Long orderId, Long itemId, int price, int count) throws NoSuchStoreException, StoreClosedException, NoSuchOrderException, NotEnoughStockException {
        //주문 Id로 주문을 찾는다.
        Order order = orderRepository.findOrderWithStoreAndOrderItemsByIdOrderByTime(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));

        Store store = order.getStore();
        List<OrderItem> orderItems = order.getOrderItems();

        //가게가 열린 상태인지 확인
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_IS_CLOSED.getMessage());
        }

        try {
            //주문에 상품 Id와 상품 가격이 같은 주문이 존재하는지 확인
            OrderItem orderItem = validateOrderItemExistenceWithItemIdAndPrice(orderItems, itemId, price);

            //주문에 상품이 이미 있다면,

            //주문에 있는 해당 상품의 수량을 늘려주고, 상품의 재고를 낮춰준다.
            Item item = orderItem.addCount(count);

            //저장
            itemRepository.save(item);
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            return savedOrderItem.getId();

        } catch (NoSuchOrderItemException e) {
            //주문에 상품이 없으면,

            //가게 Id와 상품 Id로 상품을 찾는다.
            Item findItem = itemRepository.findItemByIdAndStoreId(itemId, store.getId())
                    .orElseThrow(() -> new NoSuchItemException(ItemErrorMessage.NO_SUCH_ITEM.getMessage()));

            //새로운 주문 상품 추가
            OrderItem createdOrderItem = OrderItem.builder()
                    .store(store)
                    .order(order)
                    .item(findItem)
                    .price(price)
                    .count(count)
                .build();

            //저장
            itemRepository.save(findItem);
            OrderItem savedOrderItem = orderItemRepository.save(createdOrderItem);

            return savedOrderItem.getId();
        }
    }

    private OrderItem validateOrderItemExistenceWithItemIdAndPrice(List<OrderItem> orderItems, Long itemId, int price) {
        //주문 상품에 테이블 Id, 상품 Id, 가격이 같은 상품이 존재하는지 확인
        //해당 조건을 만족하는 상품이 없으면 예외 발생
        return orderItems.stream()
                .filter(orderItem -> orderItem.getItem().getId().equals(itemId))
                .filter(orderItem -> orderItem.getPrice() == price)
                .findFirst()
                .orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));
    }

//    private OrderItem validateOrderItemExistenceWithItemIdAndPrice(Long orderId, Long itemId, int price) throws NoSuchOrderException, NoSuchOrderItemException {
//        //주문 상품에 테이블 Id, 상품 Id, 가격이 같은 상품이 존재하는지 확인
//        //해당 조건을 만족하는 상품이 없으면 예외 발생
//        return orderItemRepository.findOrderItemByOrderIdAndItemIdAndPrice(orderId, itemId, price)
//                .orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));
//    }
}
