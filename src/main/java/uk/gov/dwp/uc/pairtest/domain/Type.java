package uk.gov.dwp.uc.pairtest.domain;

public enum Type {
    ADULT(20),
    CHILD(10),
    INFANT(0);

    private final int price;

    Type(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}





