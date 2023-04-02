package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum PaymentErrorMessage {

    PAYMENT_ALREADY_EXIST("정산이 시작되어 변경이 불가능합니다."),
    NO_SUCH_PAYMENT("해당 결제가 존재하지 않습니다.");

    private String message;

    PaymentErrorMessage(String message) {
        this.message = message;
    }
}
