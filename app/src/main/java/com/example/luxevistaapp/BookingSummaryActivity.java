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

import java.util.Random;

public class BookingSummaryActivity extends AppCompatActivity {

    private TextView confirmationIDText, roomNameText, checkInText, checkOutText, totalPriceText, userNameText, phoneNumberText;
    private Button confirmButton, modifyBookingButton, cancelBookingButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking_summary);

        // Initialize UI Components
        confirmationIDText = findViewById(R.id.roomConfirmationID);
        roomNameText = findViewById(R.id.roomName);
        checkInText = findViewById(R.id.checkInDate);
        checkOutText = findViewById(R.id.checkOutDate);
        totalPriceText = findViewById(R.id.totalPrice);
        userNameText = findViewById(R.id.userName);
        phoneNumberText = findViewById(R.id.phoneNumber);

        confirmButton = findViewById(R.id.ConfirmButton);
        modifyBookingButton = findViewById(R.id.modifyBookingButton);
        cancelBookingButton = findViewById(R.id.cancelBookingButton);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Retrieve Data from Intent
        String roomName = getIntent().getStringExtra("roomName");
        String checkInDate = getIntent().getStringExtra("checkInDate");
        String checkOutDate = getIntent().getStringExtra("checkOutDate");
        double roomPrice = getIntent().getDoubleExtra("roomPrice", 0.0);
        int totalDays = Integer.parseInt(getIntent().getStringExtra("totalDays"));

        // Calculate Total Price
        double totalPrice = totalDays * roomPrice;

        // Generate Random Confirmation ID
        String confirmationID = generateConfirmationID();

        // Set Data to TextViews
        roomNameText.setText(roomName);
        checkInText.setText(checkInDate);
        checkOutText.setText(checkOutDate);
        totalPriceText.setText(String.format("LKR %.2f", totalPrice));
        confirmationIDText.setText(confirmationID);

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
            saveBookingDetails(confirmationID, roomName, checkInDate, checkOutDate, totalDays, totalPrice, userNameText.getText().toString(), phoneNumberText.getText().toString(),useremail);
            sendConfirmationEmail(confirmationID, roomName, checkInDate, checkOutDate, totalDays, totalPrice, userNameText.getText().toString(), "user@example.com");

            Intent intent = new Intent(BookingSummaryActivity.this, BookingConfirmationActivity.class);
            intent.putExtra("confirmationID", confirmationID);
            startActivity(intent);
        });

        // Modify and Cancel Booking (Add functionality if needed)
        modifyBookingButton.setOnClickListener(v -> finish());
        cancelBookingButton.setOnClickListener(v -> finish());
    }

    private String generateConfirmationID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder confirmationID = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            confirmationID.append(characters.charAt(random.nextInt(characters.length())));
        }
        return confirmationID.toString();
    }

    private void saveBookingDetails(String confirmationNumber, String roomName, String checkInDate, String checkOutDate, int totalDays, double totalPrice, String userName, String phoneNumber,String email) {
        DatabaseReference bookingRef = databaseReference.child("Bookings").push();
        bookingRef.child("confirmationID").setValue(confirmationNumber);
        bookingRef.child("roomName").setValue(roomName);
        bookingRef.child("checkInDate").setValue(checkInDate);
        bookingRef.child("checkOutDate").setValue(checkOutDate);
        bookingRef.child("totalDays").setValue(totalDays);
        bookingRef.child("totalPrice").setValue(totalPrice);
        bookingRef.child("userName").setValue(userName);
        bookingRef.child("phoneNumber").setValue(phoneNumber);
        bookingRef.child("email").setValue(email);


    }


    private void sendConfirmationEmail(String confirmationNumber, String roomName, String checkInDate, String checkOutDate, int totalDays, double totalPrice, String userName, String userEmail) {
        // Validate email
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Compose email subject and body
        String subject = "Booking Confirmation: " + confirmationNumber;
        String message = "Dear " + userName + ",\n\n" +
                "Thank you for your booking with LuxeVista! Here are your booking details:\n\n" +
                "Confirmation Number: " + confirmationNumber + "\n" +
                "Room Name: " + roomName + "\n" +
                "Check-in Date: " + checkInDate + "\n" +
                "Check-out Date: " + checkOutDate + "\n" +
                "Total Price: LKR" + String.format("%.2f", totalPrice) + "\n\n" +
                "We look forward to hosting you.\n\n" +
                "Best Regards,\nLuxeVista Team";

        // Send email using JavaMailAPI
        JavaMailAPI javaMailAPI = new JavaMailAPI(userEmail, subject, message);
        javaMailAPI.sendEmail();
    }




}
