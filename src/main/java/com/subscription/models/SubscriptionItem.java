package main.java.com.subscription.models;

public class SubscriptionItem {
    private int id;
    private int subscription;
    private int item;
    private int quantity;
    private double price;
    private double amount;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getSubscription() {
        return subscription;
    }
    public void setSubscription(int subscription) {
        this.subscription = subscription;
    }
    public int getItem() {
        return item;
    }
    public void setItem(int item) {
        this.item = item;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
