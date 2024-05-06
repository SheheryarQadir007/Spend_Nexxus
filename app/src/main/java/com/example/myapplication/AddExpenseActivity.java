package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private String userId;
    private DatabaseReference expensesRef;
    private List<Expense> expenseList;

    private EditText expenseEditText;
    private EditText amountEditText;
    private Spinner categorySpinner;
    private CalendarUtil calendarUtil;
    private Spinner currencySpinner;
    private Button selectDateButton;
    private EditText dateEditText;
    private Button captureImageButton;
    private String picturePath;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize Firebase database reference
        expensesRef = FirebaseDatabase.getInstance().getReference().child("expenses");

        // Retrieve userId from Intent extras
        userId = getIntent().getStringExtra("userId");

        // Initialize views
        expenseEditText = findViewById(R.id.expenseEditText);
        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        currencySpinner = findViewById(R.id.currencySpinner);
        selectDateButton = findViewById(R.id.selectDateButton);
        dateEditText = findViewById(R.id.dateEditText);
        captureImageButton = findViewById(R.id.captureImageButton);

        // Initialize the category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Initialize the currency spinner
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        currencySpinner.setAdapter(currencyAdapter);

        // Initialize the CalendarUtil class
        calendarUtil = new CalendarUtil();

        // Setup Add Expense button click listener
        Button addExpenseButton = findViewById(R.id.addExpenseButton);
        addExpenseButton.setOnClickListener(v -> addExpense());

        // Setup Select Date button click listener
        selectDateButton.setOnClickListener(v -> openDatePickerDialog());

        // Setup Capture Image button click listener
        captureImageButton.setOnClickListener(v -> {
            // Provide options for the user to pick from camera or gallery
            openImagePickerDialog();
        });

        // Fetch and display expenses
        fetchExpenses();
    }

    private void fetchExpenses() {
        expensesRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenseList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Expense expense = snapshot.getValue(Expense.class);
                    if (expense != null) {
                        expenseList.add(expense);
                    }
                }
                // Now you can use expenseList to display expenses in your UI
                // For example, you can populate a ListView or RecyclerView with this data
                displayExpenses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddExpenseActivity.this, "Failed to fetch expenses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void displayExpenses() {
        LinearLayout expensesContainer = findViewById(R.id.expensesContainer);
        expensesContainer.removeAllViews(); // Clear previous views

        for (Expense expense : expenseList) {
            View expenseView = getLayoutInflater().inflate(R.layout.expense_item_layout, null);

            TextView expenseNameTextView = expenseView.findViewById(R.id.expenseNameTextView);
            TextView amountTextView = expenseView.findViewById(R.id.amountTextView);
            TextView categoryTextView = expenseView.findViewById(R.id.categoryTextView);
            TextView currencyTextView = expenseView.findViewById(R.id.currencyTextView);
            TextView dateTextView = expenseView.findViewById(R.id.dateTextView);
            TextView descriptionTextView = expenseView.findViewById(R.id.descriptionTextView);

            expenseNameTextView.setText(expense.getName());
            amountTextView.setText(String.valueOf(expense.getAmount()));
            categoryTextView.setText(expense.getCategory());
            currencyTextView.setText(expense.getCurrency());
            dateTextView.setText(expense.getDate());
            descriptionTextView.setText(expense.getDescription());

            expensesContainer.addView(expenseView);
        }
    }

    private void addExpense() {
        // Get user input
        String expenseName = expenseEditText.getText().toString().trim();
        String amount = amountEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String currency = currencySpinner.getSelectedItem().toString();
        String date = dateEditText.getText().toString().trim();

        // Validate input
        if (expenseName.isEmpty() || amount.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a new expense node with a unique ID
        DatabaseReference newExpenseRef = expensesRef.push();

        // Get the unique ID generated by Firebase
        String expenseId = newExpenseRef.getKey();

        // Create Expense object
        Expense expense = new Expense(expenseId, userId, expenseName, Double.parseDouble(amount), category, currency, date, picturePath);

        // Save expense to database
        if (expenseId != null) {
            expensesRef.child(expenseId).setValue(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddExpenseActivity.this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                        // Navigate to HomePageActivity
                        Log.d("AddExpenseActivity", "Starting HomePageActivity");
                        Intent intent = new Intent(AddExpenseActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddExpenseActivity.this, "Failed to add expense: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Failed to generate expense ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }

    private void openImagePickerDialog() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // Save the image to a file
                String picturePath = saveImageToFile(bitmap);
                // Set the picturePath variable to the path where the image is saved
                this.picturePath = picturePath;
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                // Save the image to a file
                String picturePath = saveImageToFile(bitmap);
                // Set the picturePath variable to the path where the image is saved
                this.picturePath = picturePath;
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToFile(Bitmap bitmap) {
        // Convert bitmap to byte array
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        // Get the directory for saving images
        File directory = new File(getExternalFilesDir(null) + File.separator + "ExpenseImages");
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it doesn't exist
        }

        // Create a new file to save the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File destination = new File(directory, imageFileName + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            return destination.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}