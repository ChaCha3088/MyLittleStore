package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum PaymentMethodErrorMessage {
    PAYMENT_METHODS_EXIST("결제 내역이 존재합니다."),
    PAYMENT_METHOD_AMOUNT_EXCEEDS_LEFT_TO_PAY("결제 수단 금액이 남은 결제 금액보다 큽니다.");

    private String message;

    PaymentMethodErrorMessage(String message) {
        this.message = message;
    }
}
