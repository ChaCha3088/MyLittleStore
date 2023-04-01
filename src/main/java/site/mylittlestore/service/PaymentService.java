package site.mylittlestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.mylittlestore.enumstorage.PaymentMethodType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    public List<String> getPaymentMethodTypes() {
        return Arrays.stream(PaymentMethodType.values())
                .map(PaymentMethodType::name)
                .collect(Collectors.toList());
    }
}
