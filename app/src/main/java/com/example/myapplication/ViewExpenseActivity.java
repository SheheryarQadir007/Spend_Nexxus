package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewExpenseActivity extends AppCompatActivity {
    private TextView expenseTextView;
    private Button backButton;
    private String userId;
    private List<String> expenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_expense_activity);

        // Retrieve userId from Intent extras
        userId = getIntent().getStringExtra("userId");

        // Check if userId is not null
        if (userId == null) {
            // Display an error message
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show();
            // Finish the activity
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize expenseTextView
        expenseTextView = findViewById(R.id.expenseTextView);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Initialize backButton
        backButton = findViewById(R.id.back_button);

        // Set an OnClickListener for the backButton
        backButton.setOnClickListener(view -> {
            // Create an Intent to start the homepage activity
            Intent intent = new Intent(ViewExpenseActivity.this, HomePageActivity.class);

            // Start the activity
            startActivity(intent);
        });

        // Retrieve expenses from database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("expenses").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenses.clear();
                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    String expenseName = expenseSnapshot.child("description").getValue(String.class);
                    if (expenseName != null) {
                        expenses.add(expenseName);
                    }
                }
                if (!expenses.isEmpty()) {
                    displayExpenses();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void displayExpenses() {
        StringBuilder expensesText = new StringBuilder();
        for (String expense : expenses) {
            expensesText.append(expense).append("\n");
        }
        if (expenseTextView != null) {
            expenseTextView.setText(expensesText.toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}