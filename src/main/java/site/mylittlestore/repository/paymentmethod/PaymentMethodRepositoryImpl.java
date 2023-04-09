package site.mylittlestore.repository.paymentmethod;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.mylittlestore.domain.PaymentMethod;

import javax.persistence.EntityManager;
import java.util.List;

import static site.mylittlestore.domain.QPaymentMethod.paymentMethod;

@RequiredArgsConstructor
public class PaymentMethodRepositoryImpl implements PaymentMethodRepositoryQueryDsl {
    private final EntityManager em;
    @Override
    public List<PaymentMethod> findAllByPaymentId(Long paymentId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .selectFrom(paymentMethod)
                .where(paymentMethod.payment.id.eq(paymentId))
                .fetch();
    }

}
