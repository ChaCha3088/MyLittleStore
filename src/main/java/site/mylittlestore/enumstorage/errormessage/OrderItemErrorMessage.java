package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum OrderItemErrorMessage {
    NO_SUCH_ORDER_ITEM("해당 주문이 존재하지 않습니다.");

    private String message;

    OrderItemErrorMessage(String message) {
        this.message = message;
    }
}
