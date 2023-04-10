package site.mylittlestore.domain;

import lombok.*;
import site.mylittlestore.dto.payment.PaymentDto;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.enumstorage.status.PaymentStatus;
import site.mylittlestore.exception.PaymentAmountException;
import site.mylittlestore.exception.payment.PaymentException;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    @NotNull
    @OneToMany(mappedBy = "payment")
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    @NotNull
    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long initialPaymentAmount;

//    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long desiredPaymentAmount;

    @NotNull
    @Min(value = 0, message = "가격은 0 이상이여야 합니다.")
    private Long paidPaymentAmount;

    private LocalDateTime completeDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Builder
    protected Payment(Order order, Long initialPaymentAmount) {
        this.initialPaymentAmount = initialPaymentAmount;
        this.paidPaymentAmount = 0L;
        this.paymentStatus = PaymentStatus.IN_PROGRESS;
        this.order = order;

        //연관관계 설정, 주문 상태 IN_PROGRESS로 변경
        order.createPayment(this);
    }

    //-- 비즈니스 로직 --//

    public void changePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void finishPayment() {
        this.completeDateTime = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    //-- 연관관계 메소드 --//
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethods.add(paymentMethod);
    }

    //-- Dto --//
    public PaymentDto toPaymentDto() {
        return PaymentDto.builder()
                .id(this.id)
                .paymentMethods(this.paymentMethods)
                .initialPaymentAmount(this.initialPaymentAmount)
                .desiredPaymentAmount(this.desiredPaymentAmount)
                .paidPaymentAmount(this.paidPaymentAmount)
                .completeDateTime(this.completeDateTime)
                .paymentStatus(this.paymentStatus)
                .build();
    }

}
