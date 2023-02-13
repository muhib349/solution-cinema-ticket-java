package uk.gov.dwp.uc.pairtest.exception;

public class InvalidAccountException extends InvalidPurchaseException{
    public InvalidAccountException(String message){
        super(message);
    }
}
