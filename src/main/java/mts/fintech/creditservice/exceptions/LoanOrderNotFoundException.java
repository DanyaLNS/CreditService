package mts.fintech.creditservice.exceptions;

public class LoanOrderNotFoundException extends Exception{
    public LoanOrderNotFoundException(String message) {
        super(message);
    }
    public LoanOrderNotFoundException() {
        super("ORDER_NOT_FOUND");
    }
}
