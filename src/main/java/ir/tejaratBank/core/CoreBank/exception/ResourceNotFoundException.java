package ir.tejaratBank.core.CoreBank.exception;

//404
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
