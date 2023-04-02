package site.mylittlestore.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryQueryDsl {
    Optional<Payment> findNotSuccessById(Long id);
}
