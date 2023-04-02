package site.mylittlestore.exception;

public class PaymentAmountException extends RuntimeException {
    public PaymentAmountException(String message) {
        super(message);
    }
}
