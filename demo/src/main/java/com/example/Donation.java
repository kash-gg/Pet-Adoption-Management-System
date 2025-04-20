package com.example;

import java.sql.Timestamp;

public class Donation {
    private int id;
    private int userId;
    private double amount;
    private String paymentMethod;
    private String donationType;
    private String purpose;
    private String message;
    private Timestamp date;
    private String status;

    public Donation(int id, int userId, double amount, String paymentMethod, 
                   String donationType, String purpose, String message, 
                   Timestamp date, String status) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.donationType = donationType;
        this.purpose = purpose;
        this.message = message;
        this.date = date;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDonationType() { return donationType; }
    public String getPurpose() { return purpose; }
    public String getMessage() { return message; }
    public Timestamp getDate() { return date; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setDonationType(String donationType) { this.donationType = donationType; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setMessage(String message) { this.message = message; }
    public void setDate(Timestamp date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Donation{id=%d, amount=%.2f, type=%s, purpose=%s}", 
            id, amount, donationType, purpose);
    }
} 