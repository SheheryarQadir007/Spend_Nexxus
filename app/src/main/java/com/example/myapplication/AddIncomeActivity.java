package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class AddIncomeActivity extends AppCompatActivity {

    private EditText amountEditText;
    private EditText sourceEditText;
    private Button addIncomeButton;
    private ListView incomeListView;
    private DatabaseReference incomeRef;
    private String userId; // Store the userId received from previous activity
    private List<Income> incomes = new ArrayList<>(); // Store the incomes

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        amountEditText = findViewById(R.id.amountEditText);
        sourceEditText = findViewById(R.id.sourceEditText);
        addIncomeButton = findViewById(R.id.addIncomeButton);
        incomeListView = findViewById(R.id.incomeListView);

// Initialize Firebase database reference
        incomeRef = FirebaseDatabase.getInstance().getReference().child("income");

// Get userId from previous activity
        userId = getIntent().getStringExtra("userId");

        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountEditText.getText().toString().trim();
                String sourceStr = sourceEditText.getText().toString().trim();

                if (!amountStr.isEmpty() && !sourceStr.isEmpty()) {
// Generate a unique income ID
                    String incomeId = incomeRef.push().getKey();

// Create an Income object
                    Income income = new Income(amountStr, incomeId, sourceStr, userId);

// Save income to database
                    saveIncome(income);
                } else {
                    Toast.makeText(AddIncomeActivity.this, "Please enter amount and source", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Retrieve incomes from database
        incomeRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                incomes.clear();
                for (DataSnapshot incomeSnapshot : dataSnapshot.getChildren()) {
                    Income income = incomeSnapshot.getValue(Income.class);
                    incomes.add(income);
                }
// Display incomes in ListView
                Income.IncomeAdapter adapter = new Income.IncomeAdapter(AddIncomeActivity.this, R.layout.income_list_item, incomes);
                incomeListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddIncomeActivity.this, "Failed to retrieve incomes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveIncome(Income income) {
        incomeRef.child(income.getIncomeId()).setValue(income)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddIncomeActivity.this, "Income added successfully", Toast.LENGTH_SHORT).show();
// Redirect to previous activity or any other desired activity
                        Intent intent = new Intent(AddIncomeActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish(); // Finish the current activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddIncomeActivity.this, "Failed to add income: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}