package ir.tejaratBank.core.CoreBank.exception;

//موجودی کافی نبودن
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
