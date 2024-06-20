package main.java.com.subscription.models;

import java.util.Date;
public class Subscription {
    private int id;
    private int customer;
    private int billingPeriod;
    private String billingPeriodUnit;
    private double totalDue;
    private Date activatedAt;
    private Date currentTermStart;
    private Date currentTermEnd;
    private String status;

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
    public int getBillingPeriod() {
        return billingPeriod;
    }
    public void setBillingPeriod(int billingPeriod) {
        this.billingPeriod = billingPeriod;
    }
    public String getBillingPeriodUnit() {
        return billingPeriodUnit;
    }
    public void setBillingPeriodUnit(String billingPeriodUnit) {
        this.billingPeriodUnit = billingPeriodUnit;
    }
    public double getTotalDue() {
        return totalDue;
    }
    public void setTotalDue(double totalDue) {
        this.totalDue = totalDue;
    }
    public Date getActivatedAt() {
        return activatedAt;
    }
    public void setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
    }
    public Date getCurrentTermStart() {
        return currentTermStart;
    }
    public void setCurrentTermStart(Date currentTermStart) {
        this.currentTermStart = currentTermStart;
    }
    public Date getCurrentTermEnd() {
        return currentTermEnd;
    }
    public void setCurrentTermEnd(Date currentTermEnd) {
        this.currentTermEnd = currentTermEnd;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
