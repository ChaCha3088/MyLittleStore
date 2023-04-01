package site.mylittlestore.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mylittlestore.enumstorage.PaymentMethodType;
import site.mylittlestore.enumstorage.status.PaymentMethodStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentMethod {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_METHOD_ID")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @NotNull
    private PaymentMethodType paymentMethodType;

    @NotNull
    private Long paymentMethodAmount;

    private LocalDateTime paymentMethodCompleteDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethodStatus paymentMethodStatus;


}
