package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReservationConfirmationActivity extends AppCompatActivity {

    private TextView reservationIDText;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_confirmation);

        // Initialize UI components
        reservationIDText = findViewById(R.id.reservationID);
        backToHomeButton = findViewById(R.id.backToHomeButton);

        // Retrieve the reservation ID from the intent
        String reservationID = getIntent().getStringExtra("reservationID");

        // Set the reservation ID in the TextView
        reservationIDText.setText(reservationID);

        // Handle "Back to Home" button click
        backToHomeButton.setOnClickListener(v -> {
            // Navigate back to the dashboard/home activity
            Intent intent = new Intent(ReservationConfirmationActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity to remove it from the back stack
        });
    }
}
