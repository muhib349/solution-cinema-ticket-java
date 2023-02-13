package uk.gov.dwp.uc.pairtest.exception;

public class ExceedTicketLimitException extends InvalidPurchaseException{
    public ExceedTicketLimitException(String message) {
        super(message);
    }
}
