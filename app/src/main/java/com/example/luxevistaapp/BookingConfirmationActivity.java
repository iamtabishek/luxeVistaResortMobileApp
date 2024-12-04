package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView confirmationNumberText;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_confirmation);

        // Initialize UI components
        confirmationNumberText = findViewById(R.id.confirmationNumber);
        backToHomeButton = findViewById(R.id.backToHomeButton);

        // Retrieve the confirmation number from the intent
        String confirmationNumber = getIntent().getStringExtra("confirmationID");

        // Set the confirmation number in the TextView
        confirmationNumberText.setText(confirmationNumber);

        // Handle "Back to Home" button click
        backToHomeButton.setOnClickListener(v -> {
            // Navigate back to the dashboard/home activity
            Intent intent = new Intent(BookingConfirmationActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity to remove it from the back stack
        });
    }
}
