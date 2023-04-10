package site.mylittlestore.exception;

public class WrongPaymentException extends RuntimeException {
    public WrongPaymentException(String message) {
        super(message);
    }
}
