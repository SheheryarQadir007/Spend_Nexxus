package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetBudgetActivity extends AppCompatActivity {

    private EditText budgetEditText;
    private Button setBudgetButton;
    private TextView currentBudgetTextView; // Add TextView to display current budget
    private DatabaseReference budgetRef;
    private String userId; // Store the userId received from HomePageActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);

        budgetEditText = findViewById(R.id.budgetEditText);
        setBudgetButton = findViewById(R.id.setBudgetButton);
        currentBudgetTextView = findViewById(R.id.currentBudgetTextView); // Initialize TextView

        // Initialize Firebase database reference
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget");

        // Get userId from previous activity
        userId = getIntent().getStringExtra("userId");

        // Fetch and display the current budget
        fetchCurrentBudget();

        setBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetStr = budgetEditText.getText().toString().trim();

                if (!budgetStr.isEmpty()) {
                    double budgetAmount = Double.parseDouble(budgetStr);

                    // Generate a unique budget ID
                    String budgetId = budgetRef.push().getKey();

                    // Create a Budget object
                    Budget budget = new Budget(userId, budgetId, budgetAmount);

                    // Save budget to database
                    saveBudget(budget);
                } else {
                    Toast.makeText(SetBudgetActivity.this, "Please enter a budget", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCurrentBudget() {
        // Query the database for the current budget of the user
        budgetRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through the snapshot to find the user's budget
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Budget budget = snapshot.getValue(Budget.class);
                    if (budget != null) {
                        // Display the current budget in the TextView
                        currentBudgetTextView.setText("Current Budget: " + budget.getBudgetAmount());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SetBudgetActivity.this, "Failed to fetch budget: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBudget(Budget budget) {
        budgetRef.child(budget.getBudgetId()).setValue(budget)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SetBudgetActivity.this, "Budget set successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to HomePageActivity or any other desired activity
                        Intent intent = new Intent(SetBudgetActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish(); // Finish the current activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetBudgetActivity.this, "Failed to set budget: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
