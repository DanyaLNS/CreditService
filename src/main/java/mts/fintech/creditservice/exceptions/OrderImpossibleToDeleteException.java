package mts.fintech.creditservice.exceptions;

public class OrderImpossibleToDeleteException extends Exception{
    public OrderImpossibleToDeleteException(){
        super("IMPOSSIBLE_TO_DELETE");
    }
}
