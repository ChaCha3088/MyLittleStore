package site.mylittlestore.repository.paymentmethod;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mylittlestore.domain.PaymentMethod;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>, PaymentMethodRepositoryQueryDsl {
    List<PaymentMethod> findAllByPaymentId(Long paymentId);
}
