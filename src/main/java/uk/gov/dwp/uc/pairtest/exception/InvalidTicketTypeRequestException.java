package uk.gov.dwp.uc.pairtest.exception;

public class InvalidTicketTypeRequestException extends InvalidPurchaseException{

    public InvalidTicketTypeRequestException(String message) {
        super(message);
    }
}
