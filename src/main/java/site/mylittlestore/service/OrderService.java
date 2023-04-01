package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.*;
import site.mylittlestore.domain.item.Item;
import site.mylittlestore.dto.order.OrderDtoWithOrderItemId;
import site.mylittlestore.enumstorage.errormessage.*;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.item.NoSuchItemException;
import site.mylittlestore.exception.orderitem.OrderItemException;
import site.mylittlestore.exception.store.NoSuchStoreException;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.exception.storetable.NoSuchStoreTableException;
import site.mylittlestore.exception.storetable.OrderAlreadyExistException;
import site.mylittlestore.repository.item.ItemRepository;
import site.mylittlestore.repository.orderitem.OrderItemRepository;
import site.mylittlestore.repository.payment.PaymentRepository;
import site.mylittlestore.repository.store.StoreRepository;
import site.mylittlestore.repository.order.OrderRepository;
import site.mylittlestore.repository.storetable.StoreTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final StoreTableRepository storeTableRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public OrderDtoWithOrderItemId findOrderDtoWithOrderItemIdById(Long orderId) throws NoSuchOrderException {
        Optional<Order> findOrderById = orderRepository.findUsingById(orderId);

        //주문이 없으면 예외 발생
        //Dto로 변환
        return findOrderById.orElseThrow(()
                -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()))
                .toOrderDtoWithOrderItemId();
    }

//    public OrderDtoWithOrderItemDtoWithItemNameDto findOrderDtoWithOrderItemDtoWithItemNameDtoById(Long orderId) throws NoSuchOrderException {
//        Order order = orderRepository.findOrderWithOrderItemsAndItemByIdOrderByTime(orderId)
//        //주문이 없으면 예외 발생
//                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));
//
//        //Dto로 변환
//        return order.toOrderDtoWithOrderItemDtoWithItemNameDto();
//    }

//    public OrderDtoWithOrderItemDtoWithItemFindDto findOrderDtoWithOrderItemDtoWithItemFindDtoById(Long orderId) throws NoSuchOrderException {
//        Order order = orderRepository.findOrderWithOrderItemsAndItemByIdOrderByTime(orderId)
//        //주문이 없으면 예외 발생
//                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));
//
//        //Dto로 변환
//        return order.toOrderDtoWithOrderItemDtoWithItemFindDto();
//    }

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
            throw new OrderAlreadyExistException(StoreTableErrorMessage.ORDER_ALREADY_EXIST.getMessage(), order.getId());
        }

        //가게가 열려있는지 확인
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
    public Long startPayment(Long orderId) {
        Order usingById = orderRepository.findUsingById(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));

        //Order안에 Payment가 있는지 확인

        //Payment가 있으면
        //결제가 이미 진행중이라는 뜻
        //Payment Id 반환
        if (usingById.getPayment() != null) {
            return usingById.getPayment().getId();
        }

        //Payment가 비어있으면
        //Payment 생성
        //주문에 있는 주문 상품을 모두 찾는다.
        List<OrderItem> allByOrderId = orderItemRepository.findAllByOrderId(orderId);
        //주문 상품이 없으면 예외 발생
        if (allByOrderId.isEmpty()) {
            throw new OrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage());
        }

        //합계 계산
        AtomicLong totalAmount = new AtomicLong(0);
        allByOrderId.stream().map(orderItem -> totalAmount.getAndAdd(orderItem.getPrice() * orderItem.getPrice()));

        //Payment 생성
        Payment createdPayment = Payment.builder()
                .initialPaymentAmount(totalAmount.get())
                .build();

        //저장
        Payment payment = paymentRepository.save(createdPayment);

        return payment.getId();
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
                .anyMatch(orderItem -> orderItem.getItem().getName().equals(newItemName));
    }
}
