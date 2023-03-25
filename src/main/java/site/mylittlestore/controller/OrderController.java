package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.service.OrderService;

@Controller
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;

    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}")
    public String orderInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("OrderDtoWithOrderItemDto", orderService.findOrderDtoById(orderId));

        return "orders/orderInfo";
    }
}
