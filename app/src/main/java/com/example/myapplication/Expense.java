package com.example.myapplication;

public class Expense {
    private final String expenseId;
    private final double amount;
    private final String category;
    private final String currency;
    private final String date;
    private final String description;
    private final String userId;
    private String name;
private String picturePath;
    public Expense() {
        this.expenseId = "";
        this.amount = 0.0;
        this.category = "";
        this.currency = "";
        this.date = "";
        this.description = "";
        this.userId = "";
        this.name = "";
        this.picturePath="";
    }
    // Single constructor with all parameters
    public Expense(String expenseId, String userId, String description, double amount, String category, String currency, String date, String picturePath) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.currency = currency;
        this.date = date;
        this.picturePath = picturePath;
    }

    // Getters
    public String getExpenseId() {
        return expenseId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
}