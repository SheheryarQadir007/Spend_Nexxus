package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // Declare the TextView and EditText fields at the class level
    private TextView textView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private String userId;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView and EditText fields
        textView = findViewById(R.id.textView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Initialize the Button and set OnClickListener
        // Declare loginButton at the class level
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            // Retrieve user input from EditText fields
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Verify user credentials
            verifyUserCredentials(email, password);
        });

        // Initialize the signup button and set OnClickListener
        // Declare signupButton at the class level
        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(v -> {
            // Navigate to the signup page
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Set the initial text to display
        textView.setText("Login to your account");
    }

    // Method to verify user credentials from Firebase Realtime Database
    private void verifyUserCredentials(String email, String password) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String userPassword = userSnapshot.child("password").getValue(String.class);

                    // Check if the provided email and password match any user's credentials
                    if (userEmail != null && userPassword != null && userEmail.equals(email) && userPassword.equals(password)) {
                        found = true;
                        userId = userSnapshot.getKey();
                        break;
                    }
                }

                // If credentials are verified, display success message
                if (found) {
                    textView.setText("Login successful!");
                    // Redirect to HomePageActivity and pass the userId
                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                    // Finish the current activity
                    finish();
                } else {
                    // If credentials are not verified, display error message
                    textView.setText("Invalid email or password!");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                textView.setText("Error verifying credentials!");
            }
        });
    }
}
