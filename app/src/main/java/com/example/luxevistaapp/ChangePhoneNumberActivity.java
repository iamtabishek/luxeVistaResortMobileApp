package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePhoneNumberActivity extends AppCompatActivity {

    private EditText newPhoneNumberInput, otpInput;
    private Button requestOtpButton, verifyOtpButton, backButton;
    private TextView invalidOtpTextView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phonenumber);

        // Initialize Firebase Auth and Realtime Database reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("VerifiedUsers");

        // Initialize views
        newPhoneNumberInput = findViewById(R.id.newPhoneNumberInput);
        otpInput = findViewById(R.id.otpInput);
        requestOtpButton = findViewById(R.id.requestOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        backButton = findViewById(R.id.backButton);
        invalidOtpTextView = findViewById(R.id.tv_invalid_otp);

        // Set listener for Request OTP button
        requestOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhoneNumber = newPhoneNumberInput.getText().toString().trim();

                // Validate phone number to be exactly 10 digits
                if (newPhoneNumber.isEmpty() || newPhoneNumber.length() != 10 || !newPhoneNumber.matches("\\d+")) {
                    Toast.makeText(ChangePhoneNumberActivity.this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Simulate OTP request (Replace this with actual OTP sending logic)
                Toast.makeText(ChangePhoneNumberActivity.this, "OTP requested for: " + newPhoneNumber, Toast.LENGTH_SHORT).show();

                // Show OTP input and verify button
                otpInput.setVisibility(View.VISIBLE);
                verifyOtpButton.setVisibility(View.VISIBLE);
            }
        });

        // Set listener for Verify OTP button
        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpInput.getText().toString().trim();
                String newPhoneNumber = newPhoneNumberInput.getText().toString().trim();

                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(ChangePhoneNumberActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                } else {
                    // Simulate OTP verification, replace with actual verification logic
                    if (otp.equals("1234")) { // Assuming "1234" is the correct OTP for demo purposes
                        // If OTP is correct, proceed to update phone number in Realtime Database
                        updatePhoneNumberInDatabase(newPhoneNumber);
                        Intent intent = new Intent(ChangePhoneNumberActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        invalidOtpTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Set listener for Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to the previous screen
            }
        });
    }

    private void updatePhoneNumberInDatabase(String newPhoneNumber) {
        // Check if user is authenticated
        if (auth.getCurrentUser() == null) {
            Toast.makeText(ChangePhoneNumberActivity.this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();

        // Reference to the user's data in Realtime Database's "VerifiedUsers" collection
        DatabaseReference userRef = databaseReference.child(userId);

        // Update the "phoneNumber" field in Realtime Database
        userRef.child("phoneNumber").setValue(newPhoneNumber)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangePhoneNumberActivity.this, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePhoneNumberActivity.this, "Failed to update phone number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
