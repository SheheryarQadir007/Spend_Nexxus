package com.example.myapplication;

public class Budget {
    private String userId;
    private String budgetId;
    private double budgetAmount;

    public Budget() {
        // Default constructor required for Firebase
    }

    public Budget(String userId, String budgetId, double budgetAmount) {
        this.userId = userId;
        this.budgetId = budgetId;
        this.budgetAmount = budgetAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }
}