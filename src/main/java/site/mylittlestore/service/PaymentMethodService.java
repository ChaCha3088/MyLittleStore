package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.domain.PaymentMethod;
import site.mylittlestore.dto.paymentmethod.PaymentMethodDto;
import site.mylittlestore.repository.paymentmethod.PaymentMethodRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    public List<PaymentMethodDto> findAllPaymentMethodDtosByOrderIdAndPaymentId(Long orderId, Long paymentId) {
        return paymentMethodRepository.findAllByPaymentId(paymentId)
                .stream()
                .map(PaymentMethod::toPaymentMethodDto)
                .collect(Collectors.toList());
    }
}
