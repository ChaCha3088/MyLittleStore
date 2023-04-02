package site.mylittlestore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.mylittlestore.dto.payment.PaymentViewDto;
import site.mylittlestore.enumstorage.errormessage.OrderItemErrorMessage;
import site.mylittlestore.enumstorage.errormessage.PaymentErrorMessage;
import site.mylittlestore.enumstorage.errormessage.StoreErrorMessage;
import site.mylittlestore.exception.PaymentAmountException;
import site.mylittlestore.exception.orderitem.OrderItemException;
import site.mylittlestore.exception.payment.PaymentAlreadyExistException;
import site.mylittlestore.exception.store.StoreClosedException;
import site.mylittlestore.form.PaymentCreationForm;
import site.mylittlestore.message.Message;
import site.mylittlestore.service.PaymentService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/payments/new")
    public String paymentCreationForm(@PathVariable("memberId") Long memberId, @PathVariable("storeId") Long storeId, @PathVariable("storeTableId") Long storeTableId, @PathVariable("orderId") Long orderId, Model model) {
        try {
            PaymentViewDto paymentViewDto = paymentService.startPayment(orderId);

            model.addAttribute("paymentViewDto", paymentViewDto);
            model.addAttribute("memberId", memberId);
            model.addAttribute("storeId", storeId);
            model.addAttribute("storeTableId", storeTableId);
            model.addAttribute("orderId", orderId);

            model.addAttribute("paymentCreationForm", new PaymentCreationForm());

            return "payment/paymentCreationForm";
        } catch (PaymentAlreadyExistException e) {
            //이미 결제가 진행중인 경우
            //해당 결제 페이지로 redirect
            //팝업 알림창
            model.addAttribute("messages", Message.builder()
                    .message(PaymentErrorMessage.PAYMENT_ALREADY_EXIST.getMessage())
                    .href("/members/" + memberId + "/stores/" + storeId + "/storeTables/" + e.getStoreTableId() + "/orders/" + e.getOrderId())
                    .build());
            return "message/message";
        } catch (OrderItemException e) {
            //주문 상품이 없으면
            //주문 페이지로 redirect
            //팝업 알림창
            model.addAttribute("messages", Message.builder()
                    .message(OrderItemErrorMessage.NO_SUCH_ORDER_ITEM.getMessage())
                    .href("/members/" + memberId + "/stores/" + storeId + "/storeTables/" + storeTableId + "/orders/" + e.getOrderId())
                    .build());
            return "message/message";
        } catch (StoreClosedException e) {
            //가게가 닫혀있으면
            //가게 페이지로 redirect
            //팝업 알림창
            model.addAttribute("messages", Message.builder()
                    .message(StoreErrorMessage.STORE_CLOSED.getMessage())
                    .href("/members/" + memberId + "/stores/" + storeId)
                    .build());
            return "message/message";
        }
    }

    @PostMapping("/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/payments/new")
    public String confirmPayment(@PathVariable("orderId") Long orderId, @PathVariable("paymentId") Long paymentId, @RequestBody @Valid PaymentCreationForm paymentCreationForm, BindingResult result) {
        try {
            //결제를 확정한다.
            Long confirmedPaymentId = paymentService.confirmPayment(paymentId, orderId, paymentCreationForm.getDesiredPaymentAmount());

            //결제가 확정되면
            //결제 상세 페이지로 redirect
            return "redirect:/members/{memberId}/stores/{storeId}/storeTables/{storeTableId}/orders/{orderId}/payments/{confirmedPaymentId}";
        } catch (PaymentAmountException e) {
            //원하는 결제 금액에 문제가 있으면
            //결제 생성 폼으로 redirect
            result.rejectValue("desiredPaymentAmount", "wrong.value", e.getMessage());
            return "payment/paymentCreationForm";
        }
    }
}
