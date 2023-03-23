package site.mylittlestore.exception.store;

public class StoreClosedException extends RuntimeException {
    public StoreClosedException(String message) {
        super(message);
    }
}
