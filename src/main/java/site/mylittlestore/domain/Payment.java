package site.mylittlestore.domain;

import lombok.*;
import site.mylittlestore.dto.payment.PaymentDto;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.enumstorage.status.PaymentStatus;
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
    @OneToMany(mappedBy = "payment")
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    @NotNull
    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long initialPaymentAmount;

    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long desiredPaymentAmount;

    @Min(value = 1, message = "가격은 0보다 커야합니다.")
    private Long paidPaymentAmount;

    private LocalDateTime completeDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Builder
    protected Payment(Long initialPaymentAmount) {
        this.initialPaymentAmount = initialPaymentAmount;
        this.paymentStatus = PaymentStatus.INIT;
    }

    //-- 비즈니스 로직 --//
    public void setDesiredPaymentAmount(Long desiredPaymentAmount) {
        if (desiredPaymentAmount > this.initialPaymentAmount) {
            throw new PaymentException(PaymentErrorMessage.DESIRED_PAYMENT_AMOUNT_CANNOT_BE_GREATER_THAN_INITIAL_PAYMENT_AMOUNT.getMessage());
        }
        this.desiredPaymentAmount = desiredPaymentAmount;
    }

    public void finishPayment(Long paidPaymentAmount) {
        if (paidPaymentAmount < this.desiredPaymentAmount) {
            throw new PaymentException(PaymentErrorMessage.PAID_PAYMENT_AMOUNT_CANNOT_BE_LESS_THAN_DESIRED_PAYMENT_AMOUNT.getMessage());
        }
        this.paidPaymentAmount = paidPaymentAmount;
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
