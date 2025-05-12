package com.testapp.budgettracker.model;

import java.util.Date;

public class Transaction {
    private int id;
    private String description;
    private double amount;
    private String type; // "income" or "expense"
    private Date date;

    public Transaction() {
    }

    public Transaction(String description, double amount, String type, Date date) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}