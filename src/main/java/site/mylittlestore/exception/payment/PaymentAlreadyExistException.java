package site.mylittlestore.exception.payment;

import lombok.Getter;

@Getter
public class PaymentAlreadyExistException extends RuntimeException {
    private Long storeTableId;
    private Long orderId;
    public PaymentAlreadyExistException(String message, Long storeTableId, Long orderId) {
        super(message);
        this.storeTableId = storeTableId;
        this.orderId = orderId;
    }
}
