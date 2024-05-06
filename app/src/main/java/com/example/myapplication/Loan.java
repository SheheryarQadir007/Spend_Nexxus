package com.example.myapplication;

public class Loan {
    private String loanId;
    private double amount;
    private String borrowerId;
    private String dueDate;
    private String status;
    private String userId;
    private String lenderId; // Add this field for storing the lender's ID

    public Loan() {
        // Default constructor required for Firebase
    }
    public Loan(String loanId, double amount, String borrowerId, String dueDate, String status, String userId) {
        this.loanId = loanId;
        this.amount = amount;
        this.borrowerId = borrowerId;
        this.dueDate = dueDate;
        this.status = status;
        this.userId = userId;
    }


    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getLenderId() {
        return lenderId;
    }

    public void setLenderId(String lenderId) {
        this.lenderId = lenderId;
    }
}