package main.java.com.subscription.models;

public class Card {
    private int id;
    private int customer;
    private String cardType;
    private String maskedNumber;
    private int expiryMonth;
    private int expiryYear;
    private String status;
    private int isPrimary;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCustomer() {
        return customer;
    }
    public void setCustomer(int customer) {
        this.customer = customer;
    }
    public String getCardType() {
        return cardType;
    }
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    public String getMaskedNumber() {
        return maskedNumber;
    }
    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }
    public int getExpiryMonth() {
        return expiryMonth;
    }
    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }
    public int getExpiryYear() {
        return expiryYear;
    }
    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getIsPrimary() {
        return isPrimary;
    }
    public void setIsPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }
}
