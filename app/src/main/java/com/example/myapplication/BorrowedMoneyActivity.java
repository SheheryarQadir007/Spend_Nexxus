package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

public class BorrowedMoneyActivity extends AppCompatActivity {

    private EditText amountEditText;
    private EditText lenderIdEditText;
    private EditText dueDateEditText;
    private Button recordBorrowedButton;
    private DatabaseReference loansRef;
    private String userId;
    private ListView borrowedAmountsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower);

        // Initialize Firebase database reference
        loansRef = FirebaseDatabase.getInstance().getReference().child("loans");

        // Get userId from previous activity
        userId = getIntent().getStringExtra("userId");

        // Initialize views
        amountEditText = findViewById(R.id.amountEditText);
        lenderIdEditText = findViewById(R.id.lenderIdEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        recordBorrowedButton = findViewById(R.id.recordBorrowedButton);
        borrowedAmountsListView = findViewById(R.id.borrowedAmountsListView); // Initialize the ListView

        recordBorrowedButton.setOnClickListener(v -> {
            // Retrieve user input
            String amountStr = amountEditText.getText().toString().trim();
            String lenderId = lenderIdEditText.getText().toString().trim();
            String dueDate = dueDateEditText.getText().toString().trim();

            // Validate input
            if (!amountStr.isEmpty() && !lenderId.isEmpty()) {
                double amount = Double.parseDouble(amountStr);

                // Save borrowed amount to database
                saveBorrowedAmount(amount, userId, dueDate, lenderId);
            } else {
                Toast.makeText(BorrowedMoneyActivity.this, "Please enter amount and lender ID", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch and display borrowed amounts
        fetchBorrowedAmounts();
    }

    private void saveBorrowedAmount(double amount, String borrowerId, String dueDate, String lenderId) {
        // Generate a unique loan ID
        String loanId = loansRef.push().getKey();

        // Create a Loan object with the provided data
        Loan loan = new Loan(loanId, amount, borrowerId, dueDate, "pending", lenderId);

        // Push the new loan data to the database
        loansRef.child(loanId).setValue(loan)
                .addOnSuccessListener(aVoid -> {
                    // Borrowed amount successfully recorded
                    Toast.makeText(BorrowedMoneyActivity.this, "Borrowed amount recorded successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    amountEditText.setText("");
                    lenderIdEditText.setText("");
                    dueDateEditText.setText("");
                    Intent intent = new Intent(BorrowedMoneyActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity
                })
                .addOnFailureListener(e -> {
                    // Error occurred while recording borrowed amount
                    Toast.makeText(BorrowedMoneyActivity.this, "Failed to record borrowed amount: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchBorrowedAmounts() {
        loansRef.orderByChild("borrowerId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> borrowedAmounts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Loan loan = snapshot.getValue(Loan.class);
                    if (loan != null) {
                        borrowedAmounts.add("Amount Borrowed: " + loan.getAmount() + ", Lender ID: " + loan.getLenderId() + ", Due Date: " + loan.getDueDate());
                    }
                }
                // Display borrowed amounts in ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(BorrowedMoneyActivity.this, android.R.layout.simple_list_item_1, borrowedAmounts);
                borrowedAmountsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
                Toast.makeText(BorrowedMoneyActivity.this, "Failed to fetch borrowed amounts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
