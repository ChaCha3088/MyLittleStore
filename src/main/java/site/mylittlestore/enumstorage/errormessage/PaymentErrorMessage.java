package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum PaymentErrorMessage {

    PAYMENT_ALREADY_EXIST("정산이 시작되어 변경이 불가능합니다.");

    private String message;

    PaymentErrorMessage(String message) {
        this.message = message;
    }
}
