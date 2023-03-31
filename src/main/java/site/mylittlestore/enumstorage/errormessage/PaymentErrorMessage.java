package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum PaymentErrorMessage {

    PAYMENT_ALREADY_IN_PROGRESS("결제가 이미 진행중입니다.");

    private String message;

    PaymentErrorMessage(String message) {
        this.message = message;
    }
}
