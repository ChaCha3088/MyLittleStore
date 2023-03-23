package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.dto.order.OrderDto;
import site.mylittlestore.service.StoreService;
import site.mylittlestore.service.OrderService;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final StoreService storeService;

    private final OrderService orderService;

    @GetMapping("/members/{memberId}/stores/{storeId}/orders")
    public String orderList(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("OrderDtoWithOrderItemDtoList", orderService.findAllOrderDtoWithOrderItemIdByStoreId(storeId));

        return "StoreTableList";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}")
    public String orderInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("OrderDtoWithOrderItemDto", orderService.findOrderDtoById(orderId));

        return "orders/orderInfo";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/orders/new")
    public String createOrder(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId) {
        Long savedOrderId = storeService.createStoreTable(OrderDto.builder()
                .storeId(storeId)
                .build());

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/orders/"+savedOrderId;
    }
}
