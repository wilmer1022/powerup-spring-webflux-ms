package co.com.wdgg.api.exceptions;

public class ApplicationNotFoundException extends RuntimeException {
    
    public ApplicationNotFoundException(String message) {
        super(message);
    }
}
