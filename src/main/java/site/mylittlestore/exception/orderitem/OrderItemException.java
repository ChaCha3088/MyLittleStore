package site.mylittlestore.exception.orderitem;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class OrderItemException extends RuntimeException {
    @NotNull
    private Long orderId;
    public OrderItemException(String message, Long orderId) {
        super(message);
        this.orderId = orderId;
    }
}
