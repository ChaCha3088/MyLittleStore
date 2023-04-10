package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.form.PaymentMethodCreationForm;

@Controller
@RequiredArgsConstructor
public class PaymentMethodController {

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/payments/{paymentId}/paymentMethods/new")
    public String PaymentMethodCreationForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("paymentId") Long paymentId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("storeTableId", storeTableId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("paymentId", paymentId);

        model.addAttribute("PaymentMethodCreationForm", new PaymentMethodCreationForm());
        return "paymentMethod/paymentMethodCreationForm";
    }
}
