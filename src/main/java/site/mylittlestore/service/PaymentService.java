package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.domain.Payment;
import site.mylittlestore.domain.Store;
import site.mylittlestore.dto.payment.PaymentDto;
import site.mylittlestore.enumstorage.PaymentMethodType;
import site.mylittlestore.enumstorage.errormessage.OrderErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.orderitem.NoSuchOrderItemException;
import site.mylittlestore.exception.payment.PaymentAlreadyExistException;
import site.mylittlestore.exception.payment.PaymentException;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.repository.order.OrderRepository;
import site.mylittlestore.repository.payment.PaymentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public List<String> findPaymentMethodTypes() {
        return Arrays.stream(PaymentMethodType.values())
                .map(PaymentMethodType::name)
                .collect(Collectors.toList());
    }

    /*
    SUCCESS를 제외한 payment 찾기
     */
    public PaymentDto findNotSuccessPaymentDtoById(Long id) {
        //SUCCESS를 제외한 payment 찾기
        return paymentRepository.findNotSuccessById(id)
                //없으면 예외 발생
                .orElseThrow(() -> new PaymentException(PaymentErrorMessage.NO_SUCH_PAYMENT.getMessage()))
                //Dto 변환
                .toPaymentDto();
    }

    /*
    payment 테이블 생성
     */
    @Transactional
    public Long startPayment(Long orderId) {
        Order order = findOrderWithStoreAndOrderItemsById(orderId);
        Store store = order.getStore();
        List<OrderItem> orderItems = order.getOrderItems();

        //주문 상품이 없으면 예외 발생
        if (orderItems.isEmpty()) {
            throw new NoSuchOrderItemException(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage(), order.getId());
        }

        //가게가 열려있는지 확인
        //결제 중인지 확인
        validateOrderItemChangeAbility(order, store);

        //Payment가 비어있으면
        //Payment 생성
        //합계 계산
        AtomicLong initialPaymentAmount = new AtomicLong(0);

        orderItems.stream()
                .map(orderItem -> initialPaymentAmount.addAndGet(orderItem.getPrice() * orderItem.getCount()))
                .collect(Collectors.toList());

        //Payment 생성
        Payment createdPayment = Payment.builder()
                .order(order)
                .initialPaymentAmount(initialPaymentAmount.get())
                .build();

        //저장
        Payment payment = paymentRepository.save(createdPayment);

        return payment.getId();
    }

    private static void validateOrderItemChangeAbility(Order order, Store store) {
        //가게가 열려있는지 확인
        isStoreOpen(store);

        //결제 중인지 확인
        //결제 중이면 예외 발생
        isPaymentAlreadyExists(order);
    }

    private static void isPaymentAlreadyExists(Order order) {
        if (order.getPayment() != null) {
            throw new PaymentAlreadyExistException(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage(), order.getPayment().getId(), order.getStoreTable().getId(), order.getId());
        }
    }

    private static void isStoreOpen(Store store) {
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_CLOSED.getMessage(), store.getId());
        }
    }

    private Order findOrderWithStoreById(Long orderId) {
        Order order = orderRepository.findNotDeletedAndPaidWithStoreById(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));
        return order;
    }

    private Order findOrderWithStoreAndOrderItemsById(Long orderId) {
        Order order = orderRepository.findNotDeletedAndPaidWithStoreAndOrderItemsById(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));
        return order;
    }
}
