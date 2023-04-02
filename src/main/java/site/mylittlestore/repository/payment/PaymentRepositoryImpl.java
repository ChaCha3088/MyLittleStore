package site.mylittlestore.repository.payment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.domain.Payment;
import site.mylittlestore.enumstorage.status.PaymentStatus;

import javax.persistence.EntityManager;

import java.util.Optional;

import static site.mylittlestore.domain.QPayment.payment;

@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryQueryDsl {
    private final EntityManager em;
    @Override
    public Optional<Payment> findNotSuccessById(Long id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return Optional.ofNullable(queryFactory
                    .select(payment)
                    .from(payment)
                    .where(payment.id.eq(id)
                            .and(payment.paymentStatus.ne(PaymentStatus.SUCCESS)))
                    .fetchOne());
    }
}
