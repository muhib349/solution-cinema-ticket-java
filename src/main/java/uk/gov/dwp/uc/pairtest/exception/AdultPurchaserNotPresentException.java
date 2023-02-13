package uk.gov.dwp.uc.pairtest.exception;

public class AdultPurchaserNotPresentException extends InvalidPurchaseException{
    public AdultPurchaserNotPresentException(String message) {
        super(message);
    }
}
