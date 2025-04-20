package com.example;

import java.time.LocalDate;

public class Donation {
    private int id;
    private String donorName;
    private double amount;
    private String paymentMethod;
    private String donationType;
    private String purpose;
    private String message;
    private LocalDate date;
    private String status;
    private int userId;

    public Donation(int id, String donorName, double amount, String paymentMethod, 
                   String donationType, String purpose, String message, 
                   LocalDate date, String status, int userId) {
        this.id = id;
        this.donorName = donorName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.donationType = donationType;
        this.purpose = purpose;
        this.message = message;
        this.date = date;
        this.status = status;
        this.userId = userId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDonorName() {
        return donorName;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDonationType() {
        return donationType;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public int getUserId() {
        return userId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("Donation{id=%d, donorName='%s', amount=%.2f, purpose='%s', date=%s}",
                id, donorName, amount, purpose, date);
    }
}