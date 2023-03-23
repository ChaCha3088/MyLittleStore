package site.mylittlestore.exception.orderitem;

public class NoSuchOrderItemException extends RuntimeException {
    public NoSuchOrderItemException(String message) {
        super(message);
    }
}
