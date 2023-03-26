package site.mylittlestore.domain;

import lombok.*;
import site.mylittlestore.enumstorage.PaymentMethod;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    private int paymentAmount;

    @NotNull
    private LocalDateTime paymentTime;

    @Builder
    protected Payment(PaymentMethod paymentMethod, int paymentAmount, LocalDateTime paymentTime) {
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        this.paymentTime = paymentTime;
    }
}
