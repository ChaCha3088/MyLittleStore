package site.mylittlestore.repository.payment;

import site.mylittlestore.domain.Payment;

import java.util.Optional;

public interface PaymentRepositoryQueryDsl {
    Optional<Payment> findNotSuccessById(Long id);
}
