package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.Order;
import site.mylittlestore.domain.OrderItem;
import site.mylittlestore.domain.Payment;
import site.mylittlestore.dto.payment.PaymentDto;
import site.mylittlestore.dto.payment.PaymentViewDto;
import site.mylittlestore.enumstorage.PaymentMethodType;
import site.mylittlestore.enumstorage.errormessage.OrderErrorMessage;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.exception.orderitem.OrderItemException;
import site.mylittlestore.exception.payment.PaymentException;
import site.mylittlestore.exception.store.NoSuchOrderException;
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
        Order usingById = orderRepository.findUsingById(orderId)
                .orElseThrow(() -> new NoSuchOrderException(OrderErrorMessage.NO_SUCH_ORDER.getMessage()));

        //Order안에 Payment가 있는지 확인

        //Payment가 있으면
        //결제가 이미 진행중이라는 뜻
        //Payment Id 반환
        if (usingById.getPayment() != null) {
            return PaymentViewDto.builder()
                    .id(usingById.getPayment().getId())
                    .build();
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
        AtomicLong initialPaymentAmount = new AtomicLong(0);
        allByOrderId.stream().map(orderItem -> initialPaymentAmount.getAndAdd(orderItem.getPrice() * orderItem.getPrice()));

        //Payment 생성
        Payment createdPayment = Payment.builder()
                .initialPaymentAmount(initialPaymentAmount.get())
                .build();

        //저장
        Payment payment = paymentRepository.save(createdPayment);

        return PaymentViewDto.builder()
                .id(payment.getId())
                .orderDto(usingById.toOrderDto())
                .orderItemFindDtos(allByOrderId.stream().map(orderItem -> orderItem.toOrderItemDto()).collect(Collectors.toList()))
                .initialPaymentAmount(initialPaymentAmount.get())
                .build();
    }
}
