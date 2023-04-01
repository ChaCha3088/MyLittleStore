package site.mylittlestore.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryQueryDsl {
}
