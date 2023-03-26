package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemDto;
import site.mylittlestore.dto.store.StoreOnlyDto;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.form.OrderItemCreationForm;
import site.mylittlestore.form.OrderItemForm;
import site.mylittlestore.message.Message;
import site.mylittlestore.service.ItemService;
import site.mylittlestore.service.OrderItemService;
import site.mylittlestore.service.StoreService;
import site.mylittlestore.service.OrderService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    private final ItemService itemService;

    private final OrderService orderService;

    private final StoreService storeService;

    //나중에 주문만 확인할 이유가 생길 때 만들자
//    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems")
//    public String orderItemList(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Model model) {
//        model.addAttribute("memberId", memberId);
//        model.addAttribute("storeId", storeId);
//        model.addAttribute("orderId", orderId);
//        model.addAttribute("orderItemDtoList", orderItemService.findAllOrderItemDtoWithItemNameByOrderIdOrderByTime(orderId));
//
//        return "orderItems/orderItemList";
//    }


    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/{orderItemId}")
    public String orderItemInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderItemId", orderItemId);
        model.addAttribute("orderItemDtoWithItemFindDto", orderItemService.findOrderItemByIdWithItemFindDto(orderItemId));

        return "orderItems/orderItemInfo";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/new")
    public String createOrderItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Model model) {

        StoreOnlyDto findStoreDtoById = storeService.findStoreOnlyDtoById(storeId);

        if (findStoreDtoById.getStoreStatus() == StoreStatus.CLOSE) {
            //팝업 알림창(주문하려면 가게를 열어야합니다.)
            model.addAttribute("messages", new Message(StoreErrorMessage.STORE_IS_CLOSED.getMessage(), "/members/" + memberId + "/stores/" + storeId));
            return "messages/message";
        }

        List<ItemFindDto> findAllItemCreationDtoByStoreId = itemService.findAllItemDtoByStoreId(storeId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("itemDtoList", findAllItemCreationDtoByStoreId);
        model.addAttribute("orderItemCreationForm", new OrderItemCreationForm());

        return "orderItems/orderItemCreationForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/new")
    public String createOrderItem(@PathVariable("memberId") Long memberId, @PathVariable Long storeId, @PathVariable Long orderId, @Valid OrderItemCreationForm orderItemCreationForm, BindingResult result) {

        if (result.hasErrors()) {
            return "orderItems/orderItemCreationForm";
        }

        Long createdOrderItemId = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                        .orderId(orderId)
                        .itemId(orderItemCreationForm.getItemId())
                        .price(orderItemCreationForm.getPrice())
                        .count(orderItemCreationForm.getCount())
                        .build());

        return "redirect:/members/" + memberId + "/stores/" + storeId + "/orders/" + orderId + "/orderItems/" + createdOrderItemId;
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/{orderItemId}/update")
    public String updateOrderItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("orderItemFindDto", orderItemService.findOrderItemDtoById(orderItemId));
        model.addAttribute("orderItemUpdateForm", new OrderItemForm());

        return "orderItems/orderItemUpdateForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/{orderItemId}/update")
    public String updateOrderItem(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, @Valid OrderItemForm orderItemForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("orderItemFindDto", orderItemService.findOrderItemDtoById(orderItemId));
            model.addAttribute("orderItemUpdateForm", new OrderItemForm());
            return "orderItems/orderItemUpdateForm";
        }

        Long updatedOrderItemId = orderItemService.updateOrderItemCount(OrderItemDto.builder()
                .id(orderItemId)
                .orderId(orderId) //나중에 orderId 검증할 것
                .itemId(orderItemForm.getItemId()) //나중에 itemId 검증할 것
                .price(orderItemForm.getPrice()) //나중에 price 검증할 것
                .count(orderItemForm.getCount()) //나중에 count 검증할 것
                .build());

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/orders/"+orderId+"/orderItems/"+updatedOrderItemId;
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/{orderItemId}/delete")
    public String deleteOrderItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("orderItemFindDto", orderItemService.findOrderItemDtoById(orderItemId));
        model.addAttribute("orderItemDeleteForm", new OrderItemForm());

        return "orderItems/orderItemDeleteForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/orders/{orderId}/orderItems/{orderItemId}/delete")
    public String deleteOrderItem(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, @Valid OrderItemForm orderItemDeleteForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            //
            //
            return "orderItems/orderItemDeleteForm";
        }

        orderItemService.deleteOrderItem(OrderItemDto.builder()
                .id(orderItemId)
                .orderId(orderId) //나중에 orderId 검증할 것
                .itemId(orderItemDeleteForm.getItemId()) //나중에 itemId 검증할 것
                .price(orderItemDeleteForm.getPrice()) //나중에 price 검증할 것
                .count(orderItemDeleteForm.getCount()) //나중에 count 검증할 것
                .build());

        return "redirect:/members/"+memberId+"/stores/"+storeId+"/orders/"+orderId;
    }
}
