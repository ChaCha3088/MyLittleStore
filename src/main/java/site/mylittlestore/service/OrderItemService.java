package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.domain.Store;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.orderitem.*;
import site.mylittlestore.enumstorage.errormessage.ItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.item.NotEnoughStockException;
import site.mylittlestore.exception.orderitem.OrderItemException;
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
        return orderItemRepository.findById(orderItemId)
                //주문 상품이 없으면 예외 발생
                .orElseThrow(() -> new OrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()))
                //Dto로 변환
                .toOrderItemDto();
    }

    public List<OrderItemFindDto> findAllOrderItemFindDtoByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId)
                //Dto로 변환
                .stream()
                .map(orderItem -> orderItem.toOrderItemDto())
                .collect(Collectors.toList());
    }

    public OrderItemDtoWithItemFindDto findOrderItemDtoByIdWithItemFindDto(Long orderItemId) throws OrderItemException {
        Optional<OrderItem> findOrderItemById = orderItemRepository.findWithItemById(orderItemId);

        return findOrderItemById
                //주문 상품이 없으면 예외 발생
                .orElseThrow(() -> new OrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()))
                //Dto로 변환
                .toOrderItemDtoWithItemFindDto();
    }

//    public List<OrderItemDtoWithItemNameDto> findAllOrderItemDtoWithItemNameByOrderIdOrderByTime(Long orderId) {
//        List<OrderItem> findOrderItemByOrderId = orderItemRepository.findAllOrderItemByOrderIdOrderByTime(orderId);
//
//        //Dto로 변환
//        return findOrderItemByOrderId.stream().map(OrderItem::toOrderItemDtoWithItemNameDto).collect(Collectors.toList());
//    }

    public List<OrderItemFindDto> findAllOrderItemByOrderId(Long orderId) {
        //테이블에 속한 주문 상품만 찾아야지.
        List<OrderItem> findOrderItemByOrderId = orderItemRepository.findAllOrderItemByOrderIdOrderByTime(orderId);

        //Dto로 변환
        return findOrderItemByOrderId.stream()
                .map(m -> m.toOrderItemDto())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createOrderItem(OrderItemCreationDto orderItemCreationDto) throws NoSuchStoreException, StoreClosedException, NoSuchOrderException, NotEnoughStockException {
        //주문 Id로 주문을 찾는다.
        Order order = findOrder(orderItemCreationDto.getOrderId());

        Store store = order.getStore();

        //가게가 열린 상태인지 확인
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_IS_CLOSED.getMessage());
        }

        try {
            //주문에 상품 Id와 상품 가격이 같은 주문 상품이 존재하는지 확인
            OrderItem orderItem = validateOrderItemExistenceWithOrderIdAndItemIdAndPrice(orderItemCreationDto.getOrderId(), orderItemCreationDto.getItemId(), orderItemCreationDto.getPrice());

            //주문에 상품이 이미 있다면,

            //주문에 있는 해당 상품의 수량을 늘려주고, 상품의 재고를 낮춰준다.
            Item item = orderItem.addCount(orderItemCreationDto.getCount());

            //저장
            itemRepository.save(item);
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            return savedOrderItem.getId();

        } catch (OrderItemException e) {
            //주문에 상품이 없으면,

            //가게 Id와 상품 Id로 상품을 찾는다.
            Item findItem = itemRepository.findItemByIdAndStoreId(orderItemCreationDto.getItemId(), store.getId())
                    .orElseThrow(() -> new NoSuchItemException(ItemErrorMessage.NO_SUCH_ITEM.getMessage()));

            //새로운 주문 상품 추가
            OrderItem createdOrderItem = OrderItem.builder()
                    .store(store)
                    .order(order)
                    .item(findItem)
                    .price(orderItemCreationDto.getPrice())
                    .count(orderItemCreationDto.getCount())
                .build();

            //저장
            itemRepository.save(findItem);
            OrderItem savedOrderItem = orderItemRepository.save(createdOrderItem);

            return savedOrderItem.getId();
        }
    }

    /**
     * 주문 상품을 수정하기 위해서는 상품 Id, 상품 가격이 같아야 한다.
     * 따라서 가격이 한번 정해지면, 수량만 변경 가능하다.
     * @param orderItemDto
     * @return
     * @throws NoSuchStoreException
     * @throws StoreClosedException
     * @throws OrderItemException
     */
    @Transactional
    public Long updateOrderItemCount(OrderItemDto orderItemDto) throws NoSuchStoreException, StoreClosedException, OrderItemException {
        //주문 Id로 주문을 찾는다.
        Order order = findOrder(orderItemDto.getOrderId());

        Store store = order.getStore();

        //가게가 열린 상태인지 확인
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_IS_CLOSED.getMessage());
        }

        //주문에 상품 Id와 가격이 같은 주문 상품이 존재하는지 확인
        OrderItem orderItem = validateOrderItemExistenceWithOrderIdAndOrderItemIdAndItemIdAndPrice(orderItemDto.getOrderId(), orderItemDto.getId(), orderItemDto.getItemId(), orderItemDto.getPrice());

        Optional<Integer> price = Optional.ofNullable(orderItemDto.getPrice());
        Optional<Integer> count = Optional.ofNullable(orderItemDto.getCount());

        price.ifPresent(orderItem::updatePrice);
        count.ifPresent(orderItem::updateCount);

        //저장
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return savedOrderItem.getId();
    }

    /**
     * 주문 상품을 삭제하기 위해서는 상품 Id, 상품 가격이 같아야 한다.
     * @param orderItemDto
     * @throws EmptyResultDataAccessException
     */
    @Transactional
    public void deleteOrderItem(OrderItemDto orderItemDto) throws EmptyResultDataAccessException {
        OrderItemDto orderItemDto1 = orderItemDto;

        Order order = findOrder(orderItemDto.getOrderId());

        //주문에 상품 Id, 상품 가격이 같은 주문 상품이 존재하는지 확인하고 삭제
        OrderItem orderItem = validateOrderItemExistenceWithOrderIdAndOrderItemIdAndItemIdAndPrice(order.getId(), orderItemDto.getId(), orderItemDto.getItemId(), orderItemDto.getPrice());

        try {
            //해당 상품의 재고를 늘려주고, 주문 상품을 삭제한다.
            Item item = orderItem.getItem();
            item.increaseStock(orderItem.getCount());

            //저장
            itemRepository.save(item);
        } catch (NullPointerException e) {
            //상품이 없으면, 주문 상품만 삭제
        } finally {
            orderItemRepository.deleteByChangingStatus(orderItem.getId());
        }
    }

    private Order findOrder(Long orderId) {
        Order order = orderRepository.findOrderWithStoreById(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));
        return order;
    }

    private OrderItem validateOrderItemExistenceWithOrderIdAndItemIdAndPrice(Long orderId, Long itemId, int price) {
        //주문 상품에 주문 Id, 상품 Id, 가격이 같은 상품이 존재하는지 확인
        //해당 조건을 만족하는 주문 상품이 없으면 예외 발생
        return orderItemRepository.findByOrderIdAndItemIdAndPrice(orderId, itemId, price)
                .orElseThrow(() -> new OrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));
    }

    private OrderItem validateOrderItemExistenceWithOrderIdAndOrderItemIdAndItemIdAndPrice(Long orderId, Long orderItemId, Long itemId, int price) {
        //주문 상품에 주문 Id, 주문 상품 Id, 상품 Id, 가격이 같은 상품이 존재하는지 확인
        //해당 조건을 만족하는 상품이 없으면 예외 발생
        return orderItemRepository.findByOrderIdAndOrderItemIdAndItemIdAndPrice(orderId, orderItemId, itemId, price)
                .orElseThrow(() -> new OrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));
    }

//    private OrderItem validateOrderItemExistenceWithItemIdAndPrice(Long orderId, Long itemId, int price) throws NoSuchOrderException, NoSuchOrderItemException {
//        //주문 상품에 테이블 Id, 상품 Id, 가격이 같은 상품이 존재하는지 확인
//        //해당 조건을 만족하는 상품이 없으면 예외 발생
//        return orderItemRepository.findOrderItemByOrderIdAndItemIdAndPrice(orderId, itemId, price)
//                .orElseThrow(() -> new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage()));
//    }
}
