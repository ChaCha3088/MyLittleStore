package site.mylittlestore.enumstorage.errormessage;

import lombok.Getter;

@Getter
public enum StoreTableErrorMessage {

    NO_SUCH_STORE_TABLE("해당하는 테이블이 없습니다."),
    ORDER_ALREADY_EXIST("이미 주문이 존재합니다."),
    STILL_ORDER_OR_ORDER_ITEM_EXIST("아직 주문, 주문 상품이 존재합니다.");

    private String message;

    StoreTableErrorMessage(String message) {
        this.message = message;
    }
}
