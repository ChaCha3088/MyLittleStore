package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.domain.Payment;
import site.mylittlestore.domain.Store;
import site.mylittlestore.dto.payment.PaymentDto;
import site.mylittlestore.dto.payment.PaymentViewDto;
import site.mylittlestore.enumstorage.PaymentMethodType;
import site.mylittlestore.enumstorage.errormessage.OrderErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.PaymentStatus;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.orderitem.NoSuchOrderItemException;
import site.mylittlestore.exception.payment.PaymentAlreadyExistException;
import site.mylittlestore.exception.payment.PaymentException;
import site.mylittlestore.exception.store.NoSuchOrderException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.repository.order.OrderRepository;
import site.mylittlestore.repository.orderitem.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;

    public List<String> getPaymentMethodTypes() {
        return Arrays.stream(PaymentMethodType.values())
                .map(PaymentMethodType::name)
                .collect(Collectors.toList());
    }

    public PaymentDto findNotSuccessPaymentDtoById(Long id) {
        //성공을 제외한 payment 찾기
        return paymentRepository.findNotSuccessById(id)
                //없으면 예외 발생
                .orElseThrow(() -> new PaymentException(PaymentErrorMessage.NO_SUCH_PAYMENT.getMessage()))
                //Dto 변환
                .toPaymentDto();

    }

    @Transactional
    public PaymentViewDto startPayment(Long orderId) {
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
        orderItems.stream().map(orderItem -> initialPaymentAmount.getAndAdd(orderItem.getPrice() * orderItem.getPrice()));

        //Payment 생성
        Payment createdPayment = Payment.builder()
                .initialPaymentAmount(initialPaymentAmount.get())
                .build();

        //저장
        Payment payment = paymentRepository.save(createdPayment);

        return PaymentViewDto.builder()
                .id(payment.getId())
                .orderDto(order.toOrderDto())
                .orderItemFindDtos(orderItems.stream().map(orderItem -> orderItem.toOrderItemFindDto()).collect(Collectors.toList()))
                .initialPaymentAmount(initialPaymentAmount.get())
                .build();
    }

    @Transactional
    public Long confirmPayment(Long paymentId, Long orderId, Long desiredPaymentAmount) {
        Order order = findOrderWithStoreById(orderId);
        Store store = order.getStore();

        //가게가 열려있는지 확인
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_CLOSED.getMessage(), store.getId());
        }

        //결제를 찾는다.
        Payment payment = paymentRepository.findNotSuccessById(paymentId)
                //없으면 예외 발생
                .orElseThrow(() -> new PaymentException(PaymentErrorMessage.NO_SUCH_PAYMENT.getMessage()));

        //원하는 결제 금액 기록
        //원하는 결제 금액이 초기 결제 금액과 같거나 작은지 확인
        payment.setDesiredPaymentAmount(desiredPaymentAmount);

        //문제 없으면
        //결제 상태를 결제 중으로 변경
        payment.changePaymentStatus(PaymentStatus.IN_PROCESS);

        return payment.getId();
    }

    private static void validateOrderItemChangeAbility(Order order, Store store) {
        //가게가 열려있는지 확인
        if (store.getStoreStatus().equals(StoreStatus.CLOSE)) {
            throw new StoreClosedException(StoreErrorMessage.STORE_CLOSED.getMessage(), store.getId());
        }

        //결제 중인지 확인
        //결제 중이면 예외 발생
        if (order.getPayment() != null) {
            throw new PaymentAlreadyExistException(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage(), order.getPayment().getId(), order.getStoreTable().getId(), order.getId());
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
