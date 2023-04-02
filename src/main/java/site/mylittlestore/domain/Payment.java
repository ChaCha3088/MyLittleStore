package site.mylittlestore.domain;

import lombok.*;
import site.mylittlestore.dto.payment.PaymentDto;
import site.mylittlestore.enumstorage.status.PaymentStatus;

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
    private Long finalPaymentAmount;

    private LocalDateTime completeDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Builder
    protected Payment(Long initialPaymentAmount) {
        this.initialPaymentAmount = initialPaymentAmount;
        this.paymentStatus = PaymentStatus.INIT;
    }

    //-- Dto --//
    public PaymentDto toPaymentDto() {
        return PaymentDto.builder()
                .id(this.id)
                .paymentMethods(this.paymentMethods)
                .initialPaymentAmount(this.initialPaymentAmount)
                .finalPaymentAmount(this.finalPaymentAmount)
                .completeDateTime(this.completeDateTime)
                .paymentStatus(this.paymentStatus)
                .build();
    }

}
