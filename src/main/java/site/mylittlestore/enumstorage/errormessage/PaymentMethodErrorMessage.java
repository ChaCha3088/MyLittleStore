package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum PaymentMethodErrorMessage {
    PAYMENT_METHODS_EXIST("결제 내역이 존재합니다.");

    private String message;

    PaymentMethodErrorMessage(String message) {
        this.message = message;
    }
}
