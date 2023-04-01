package site.mylittlestore.domain;

import lombok.*;
import site.mylittlestore.enumstorage.PaymentMethodType;
import site.mylittlestore.enumstorage.status.PaymentStatus;

import javax.persistence.*;
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
    private Long initialAmount;

    private Long finalAmount;

    private LocalDateTime completeDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Builder
    protected Payment(Long initialAmount) {
        this.initialAmount = initialAmount;
        this.paymentStatus = PaymentStatus.INIT;
    }
}
