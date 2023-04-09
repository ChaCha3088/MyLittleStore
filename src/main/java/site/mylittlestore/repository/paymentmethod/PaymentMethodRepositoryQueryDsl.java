package site.mylittlestore.repository.paymentmethod;

import site.mylittlestore.domain.PaymentMethod;

import java.util.List;

public interface PaymentMethodRepositoryQueryDsl {
    List<PaymentMethod> findAllByPaymentId(Long paymentId);
}
