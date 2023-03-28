package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.mylittlestore.dto.store.StoreOnlyDto;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.message.Message;
import site.mylittlestore.service.OrderItemService;
import site.mylittlestore.service.OrderService;
import site.mylittlestore.service.StoreService;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final StoreService storeService;

    private final OrderService orderService;

    private final OrderItemService orderItemService;

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}")
    public String orderInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("orderDtoWithOrderItemId", orderService.findOrderDtoWithOrderItemIdById(orderId));
        model.addAttribute("orderItemDtoWithItemNameDtos", orderItemService.findAllOrderItemDtoWithItemNameDtoByOrderId(orderId));

        return "orders/orderInfo";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/new")
    public String createOrder(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, Model model) {
        StoreOnlyDto storeOnlyDtoById = storeService.findStoreOnlyDtoById(storeId);

        if (storeOnlyDtoById.getStoreStatus() == StoreStatus.CLOSE) {
            //팝업 알림창(주문하려면 가게를 열어야합니다.)
            model.addAttribute("messages", new Message(StoreErrorMessage.STORE_IS_CLOSED.getMessage(), "/members/" + memberId + "/stores/" + storeId));
            return "messages/message";
        }

        Long createdOrderId = orderService.createOrder(storeId, storeTableId);

        return "redirect:/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + createdOrderId;
    }
}
