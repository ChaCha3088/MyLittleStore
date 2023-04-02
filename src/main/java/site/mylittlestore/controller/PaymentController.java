package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.dto.payment.PaymentViewDto;
import site.mylittlestore.service.PaymentService;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/payment/new")
    public String paymentCreationForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, Model model) {
        PaymentViewDto paymentViewDto = paymentService.startPayment(orderId);

        model.addAttribute("paymentViewDto", paymentViewDto);

        return "payments/paymentCreationForm";
    }
}
