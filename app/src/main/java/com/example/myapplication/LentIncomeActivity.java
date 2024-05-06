package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LentIncomeActivity extends AppCompatActivity {

    private EditText amountEditText;
    private EditText borrowerIdEditText;
    private EditText dueDateEditText;
    private Button recordIncomeButton;
    private DatabaseReference loansRef;
    private String userId; // Store the userId received from HomePageActivity
    private ListView lentIncomeListView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lended_income);

        // Get userId from previous activity
        userId = getIntent().getStringExtra("userId");
        //Toast.makeText(LentIncomeActivity.this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
        // Initialize Firebase database reference
        loansRef = FirebaseDatabase.getInstance().getReference().child("loans");

        // Initialize views
        amountEditText = findViewById(R.id.amountEditText);
        borrowerIdEditText = findViewById(R.id.borrowerIdEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        recordIncomeButton = findViewById(R.id.recordIncomeButton);

        recordIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input
                String amountStr = amountEditText.getText().toString().trim();
                String borrowerId = borrowerIdEditText.getText().toString().trim();
                String dueDate = dueDateEditText.getText().toString().trim();

                // Validate input
                if (!amountStr.isEmpty() && !borrowerId.isEmpty()) {
                    double amount = Double.parseDouble(amountStr);

                    // Save lent income to database
                    saveLentIncome(amount, borrowerId, dueDate);
                } else {
                    Toast.makeText(LentIncomeActivity.this, "Please enter amount and borrower ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Initialize ListView
        lentIncomeListView = findViewById(R.id.lentIncomeListView);

        // Fetch and display lent incomes
        fetchLentIncomes();
    }

    private void saveLentIncome(double amount, String borrowerId, String dueDate) {
        // Generate a unique loan ID
        String loanId = loansRef.push().getKey();

        // Create a Loan object with the provided data
        Loan loan = new Loan(loanId, amount, borrowerId, dueDate, "pending", userId);

        // Push the new loan data to the database
        loansRef.child(loanId).setValue(loan)
                .addOnSuccessListener(aVoid -> {
                    // Lent income successfully recorded
                    Toast.makeText(LentIncomeActivity.this, "Lent income recorded successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    amountEditText.setText("");
                    borrowerIdEditText.setText("");
                    dueDateEditText.setText("");
                    Intent intent = new Intent(LentIncomeActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity
                })
                .addOnFailureListener(e -> {
                    // Error occurred while recording lent income
                    Toast.makeText(LentIncomeActivity.this, "Failed to record lent income: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void fetchLentIncomes() {
        loansRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> lentIncomes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Loan loan = snapshot.getValue(Loan.class);
                    if (loan != null && loan.getBorrowerId() != null && !loan.getBorrowerId().equals(userId)) {
                        lentIncomes.add("Amount: " + loan.getAmount() + ", Borrower ID: " + loan.getBorrowerId() + ", Due Date: " + loan.getDueDate());
                    }
                }
                // Display lent incomes in ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LentIncomeActivity.this, android.R.layout.simple_list_item_1, lentIncomes);
                lentIncomeListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
                Toast.makeText(LentIncomeActivity.this, "Failed to fetch lent incomes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}