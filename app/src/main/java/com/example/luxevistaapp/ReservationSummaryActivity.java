package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Random;

public class ReservationSummaryActivity extends AppCompatActivity {

    private TextView reservationIDText, serviceNameText, totalPriceText, userNameText, phoneNumberText;
    private Button confirmButton, modifyReservationButton, cancelReservationButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reservation_summary);

        // Initialize UI Components
        reservationIDText = findViewById(R.id.serviceReservationID);
        serviceNameText = findViewById(R.id.reservationName);
        totalPriceText = findViewById(R.id.totalPrice);
        userNameText = findViewById(R.id.userName);
        phoneNumberText = findViewById(R.id.phoneNumber);

        confirmButton = findViewById(R.id.ConfirmButton);
        modifyReservationButton = findViewById(R.id.modifyReservationButton);
        cancelReservationButton = findViewById(R.id.cancelReservationButton);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Retrieve Data from Intent
        String serviceName = getIntent().getStringExtra("serviceName");
        double servicePrice = getIntent().getDoubleExtra("servicePrice", 0.0);
        int totalHours = Integer.parseInt(getIntent().getStringExtra("totalHours"));
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");


        // Calculate Total Price
        double totalPrice = totalHours * servicePrice;

        // Generate Random Confirmation Number
        String reservationID = generateReservationID();

        // Set Data to TextViews
        serviceNameText.setText(serviceName);
        totalPriceText.setText(String.format("LKR %.2f", totalPrice));
        reservationIDText.setText(reservationID);

        // Fetch Logged-In User's Details
        String userId = auth.getCurrentUser().getUid();
        databaseReference.child("VerifiedUsers").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userName = task.getResult().child("name").getValue(String.class);
                String phoneNumber = task.getResult().child("phoneNumber").getValue(String.class);
                String email = task.getResult().child("email").getValue(String.class);

                userNameText.setText(userName);
                phoneNumberText.setText(phoneNumber);
                useremail=email;

            }
        });


        // Confirm Booking
        confirmButton.setOnClickListener(v -> {
            saveBookingDetails(reservationID, serviceName, userNameText.getText().toString(), phoneNumberText.getText().toString(),useremail,date,time,totalHours, totalPrice);

            Intent intent = new Intent(ReservationSummaryActivity.this, ReservationConfirmationActivity.class);
            intent.putExtra("reservationID", reservationID);
            startActivity(intent);
        });

        // Modify and Cancel Booking (Add functionality if needed)
        modifyReservationButton.setOnClickListener(v -> finish());
        cancelReservationButton.setOnClickListener(v -> finish());
    }

    private String generateReservationID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder reservationID = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            reservationID.append(characters.charAt(random.nextInt(characters.length())));
        }
        return reservationID.toString();
    }

    private void saveBookingDetails(String reservationID, String serviceName,  String userName, String phoneNumber,String email, String date, String time, int totalHours, double totalPrice) {
        DatabaseReference ReservationRef = databaseReference.child("Reservations").push();
        ReservationRef.child("reservationID").setValue(reservationID);
        ReservationRef.child("serviceName").setValue(serviceName);
        ReservationRef.child("userName").setValue(userName);
        ReservationRef.child("phoneNumber").setValue(phoneNumber);
        ReservationRef.child("email").setValue(email);
        ReservationRef.child("date").setValue(date);
        ReservationRef.child("time").setValue(time);
        ReservationRef.child("totalHours").setValue(totalHours);
        ReservationRef.child("totalPrice").setValue(totalPrice);


    }







}
