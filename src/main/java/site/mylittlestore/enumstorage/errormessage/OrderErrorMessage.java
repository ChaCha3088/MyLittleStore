package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum OrderErrorMessage {

    NO_SUCH_ORDER("해당하는 주문이 없습니다.");

    private String message;

    OrderErrorMessage(String message) {
        this.message = message;
    }
}
