package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        // Initialize buttons
        Button btnAddExpense = findViewById(R.id.btnAddExpense);
      //  Button btnViewExpense = findViewById(R.id.btnViewExpense);
        Button btnSetBudget = findViewById(R.id.btnSetBudget);
        Button btnAddIncome = findViewById(R.id.btnAddIncome);
        Button btnLentIncome = findViewById(R.id.btnLendedIncome);
        Button btnBorrowedMoney = findViewById(R.id.btnBorrowedMoney);
        // Retrieve userId from Intent extras
        userId = getIntent().getStringExtra("userId");
        //Toast.makeText(HomePageActivity.this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
        // Set OnClickListener for each button
        btnAddExpense.setOnClickListener(v -> {
            // Start AddExpenseActivity and pass userId as an extra
            Intent intent = new Intent(HomePageActivity.this, AddExpenseActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        /*btnViewExpense.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, ViewExpenseActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });*/

        btnSetBudget.setOnClickListener(v -> {
            // Navigate to SetBudgetActivity
            Intent intent = new Intent(HomePageActivity.this, SetBudgetActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnAddIncome.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, AddIncomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnLentIncome.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, LentIncomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnBorrowedMoney.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, BorrowedMoneyActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}
