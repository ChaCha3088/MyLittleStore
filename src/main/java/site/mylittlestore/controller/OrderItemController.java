package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.mylittlestore.dto.item.ItemFindDto;
import site.mylittlestore.dto.orderitem.OrderItemCreationDto;
import site.mylittlestore.dto.orderitem.OrderItemDto;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.enumstorage.status.StoreStatus;
import site.mylittlestore.exception.payment.PaymentAlreadyExistException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.form.OrderItemCreationForm;
import site.mylittlestore.form.OrderItemForm;
import site.mylittlestore.message.Confirm;
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
//    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems")
//    public String orderItemList(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, Model model) {
//        model.addAttribute("memberId", memberId);
//        model.addAttribute("storeId", storeId);
//        model.addAttribute("orderId", orderId);
//        model.addAttribute("orderItemDtos", orderItemService.findAllOrderItemDtoWithItemNameByOrderIdOrderByTime(orderId));
//
//        return "orderItems/orderItemList";
//    }


    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}")
    public String orderItemInfo(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("storeTableId", storeTableId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderItemId", orderItemId);
        model.addAttribute("orderItemDtoWithItemFindDto", orderItemService.findOrderItemDtoByIdWithItemFindDto(orderItemId));
        model.addAttribute("orderItemForm", new OrderItemForm());

        return "orderItems/orderItemInfo";
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new")
    public String createOrderItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, Model model) {
        //가게가 닫혀있으면, 가게를 열어야합니다. 메시지 출력
        String x = isStoreOpen(memberId, storeId, model);
        if (x != null) return x;

        //정산이 시작되었는지 확인
        String x1 = isPaymentStarted(memberId, storeId, storeTableId, orderId, model);
        if (x1 != null) return x1;

        List<ItemFindDto> findAllItemCreationDtoByStoreId = itemService.findAllItemDtoByStoreId(storeId);
        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("storeTableId", storeTableId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("itemDtos", findAllItemCreationDtoByStoreId);
        model.addAttribute("orderItemCreationForm", new OrderItemCreationForm());

        return "orderItems/orderItemCreationForm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/new")
    public String createOrderItem(@PathVariable("memberId") Long memberId, @PathVariable Long storeId, @PathVariable Long storeTableId, @PathVariable Long orderId, @RequestBody @Valid OrderItemCreationForm orderItemCreationForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "orderItems/orderItemCreationForm";
        }

        try {
            Long createdOrderItemId = orderItemService.createOrderItem(OrderItemCreationDto.builder()
                    .orderId(orderId)
                    .itemId(orderItemCreationForm.getItemId())
                    .price(orderItemCreationForm.getPrice())
                    .count(orderItemCreationForm.getCount())
                    .build());

            return "redirect:/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + orderId;
        } catch (PaymentAlreadyExistException e) {  //진행중인 정산이 존재하면, 정산이 시작되어 변경이 불가능합니다.
            //팝업 알림창
            model.addAttribute("messages", new Message(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage(), "/members/" + memberId + "/stores/" + storeId + "/storeTables/" + e.getStoreTableId() + "/orders/" + e.getOrderId()));
            return "messages/message";
        } catch (StoreClosedException e) {  //가게가 닫혀있으면, 가게를 열어야합니다.
            //팝업 알림창
            model.addAttribute("messages", new Message(StoreErrorMessage.STORE_CLOSED.getMessage(), "/members/" + memberId + "/stores/" + storeId));
            return "messages/message";
        }
    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/update")
    public String updateOrderItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
        //가게가 닫혀있으면, 가게를 열어야합니다. 메시지 출력
        String x = isStoreOpen(memberId, storeId, model);
        if (x != null) return x;

        //정산이 시작되었는지 확인
        String x1 = isPaymentStarted(memberId, storeId, storeTableId, orderId, model);
        if (x1 != null) return x1;

        model.addAttribute("memberId", memberId);
        model.addAttribute("storeId", storeId);
        model.addAttribute("orderItemFindDto", orderItemService.findOrderItemDtoById(orderItemId));
        model.addAttribute("orderItemForm", new OrderItemForm());

        return "orderItems/orderItemUpdateForm";
    }



    @PostMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/update")
    public String updateOrderItem(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, @RequestBody @Valid OrderItemForm orderItemForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("orderItemFindDto", orderItemService.findOrderItemDtoById(orderItemId));
            return "orderItems/orderItemUpdateForm";
        }

        try {
            Long updatedOrderItemId = orderItemService.updateOrderItemCount(OrderItemDto.builder()
                    .id(orderItemId)
                    .orderId(orderItemForm.getId()) //나중에 orderId 검증할 것
                    .itemId(orderItemForm.getItemId()) //나중에 itemId 검증할 것
                    .price(orderItemForm.getPrice()) //나중에 price 검증할 것
                    .count(orderItemForm.getCount()) //나중에 count 검증할 것
                    .build());

            return "redirect:/members/"+memberId+"/stores/"+storeId+"/storeTables/"+storeTableId+"/orders/"+orderId+"/orderItems/"+updatedOrderItemId;
        } catch (PaymentAlreadyExistException e) {  //진행중인 정산이 존재하면, 정산이 시작되어 변경이 불가능합니다.
            //팝업 알림창
            model.addAttribute("messages", new Message(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage(), "/members/" + memberId + "/stores/" + storeId + "/storeTables/" + e.getStoreTableId() + "/orders/" + e.getOrderId()));
            return "messages/message";
        } catch (StoreClosedException e) {  //가게가 닫혀있으면, 가게를 열어야합니다.
            //팝업 알림창
            model.addAttribute("messages", new Message(StoreErrorMessage.STORE_CLOSED.getMessage(), "/members/" + memberId + "/stores/" + storeId));
            return "messages/message";
        }
    }

//    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/delete")
//    public String deleteOrderItemForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
//        model.addAttribute("memberId", memberId);
//        model.addAttribute("storeId", storeId);
//        model.addAttribute("orderItemFindDto", orderItemService.findOrderItemDtoById(orderItemId));
//        model.addAttribute("orderItemDeleteForm", new OrderItemForm());
//
//        return "orderItems/orderItemDeleteForm";
//    }

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/delete")
    public String deleteOrderItemConfirm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, Model model) {
        //가게가 닫혀있으면, 가게를 열어야합니다. 메시지 출력
        String x = isStoreOpen(memberId, storeId, model);
        if (x != null) return x;

        //정산이 시작되었는지 확인
        String x1 = isPaymentStarted(memberId, storeId, storeTableId, orderId, model);
        if (x1 != null) return x1;

        //팝업 확인창(주문 상품 삭제 확인창)
        model.addAttribute("messages", new Confirm(OrderItemErrorMessage.CONFIRM_DELETE_ORDER_ITEM.getMessage(), "/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + orderId + "/orderItems/" + orderItemId + "/delete", "/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + orderId));
        return "messages/confirm";
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/orderItems/{orderItemId}/delete")
    public String deleteOrderItem(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, @PathVariable("orderItemId") Long orderItemId, @RequestBody @Valid OrderItemForm orderItemForm, BindingResult result, Model model) {

        try {
            orderItemService.deleteOrderItem(OrderItemDto.builder()
                    .id(orderItemId)
                    .orderId(orderId) //나중에 orderId 검증할 것
                    .itemId(orderItemForm.getItemId()) //나중에 itemId 검증할 것
                    .price(orderItemForm.getPrice()) //나중에 price 검증할 것
                    .count(orderItemForm.getCount()) //나중에 count 검증할 것
                    .build());

            return "redirect:/members/"+memberId+"/stores/"+storeId+"/storeTables/"+storeTableId+"/orders/"+orderId;
        } catch (PaymentAlreadyExistException e) {  //진행중인 정산이 존재하면, 정산이 시작되어 변경이 불가능합니다.
            //팝업 알림창
            model.addAttribute("messages", new Message(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage(), "/members/" + memberId + "/stores/" + storeId + "/storeTables/" + e.getStoreTableId() + "/orders/" + e.getOrderId()));
            return "messages/message";
        } catch (StoreClosedException e) {  //가게가 닫혀있으면, 가게를 열어야합니다.
            //팝업 알림창
            model.addAttribute("messages", new Message(StoreErrorMessage.STORE_CLOSED.getMessage(), "/members/" + memberId + "/stores/" + storeId));
            return "messages/message";
        }
    }

    private String isStoreOpen(Long memberId, Long storeId, Model model) {
        //가게가 닫혀있으면, 가게를 열어야합니다. 메시지 출력
        if (storeService.findStoreOnlyDtoById(storeId).getStoreStatus() == StoreStatus.CLOSE) {
            //팝업 알림창
            model.addAttribute("messages", new Message(StoreErrorMessage.STORE_CLOSED.getMessage(), "/members/" + memberId + "/stores/" + storeId));
            return "messages/message";
        }
        return null;
    }

    private String isPaymentStarted(Long memberId, Long storeId, Long storeTableId, Long orderId, Model model) {
        //정산이 시작되었는지 확인
        //진행중인 정산이 존재하면
        if (orderService.findOrderDtoById(orderId).getPaymentId() != null) {
            //팝업 알림창
            model.addAttribute("messages", new Message(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage(), "/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + orderId));
            return "messages/message";
        }
        return null;
    }
}
