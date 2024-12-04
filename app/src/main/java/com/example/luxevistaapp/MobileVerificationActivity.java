package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MobileVerificationActivity extends AppCompatActivity {

    private EditText phoneNumberInput, nicInput, otpInput;
    private Button requestOtpButton, verifyOtpButton;
    private DatabaseReference databaseReference;
    private String userId;
    private String generatedOtp = "1234";  // Simulated OTP for demonstration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);

        // Initialize views
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        nicInput = findViewById(R.id.nicInput);
        otpInput = findViewById(R.id.otpInput);
        requestOtpButton = findViewById(R.id.requestOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        otpInput.setVisibility(View.GONE);
        verifyOtpButton.setVisibility(View.GONE);

        userId = getIntent().getStringExtra("userId");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        requestOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberInput.getText().toString().trim();

                // Validate phone number to be 10 digits
                if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d+")) {
                    Toast.makeText(MobileVerificationActivity.this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show OTP input fields after requesting OTP
                otpInput.setVisibility(View.VISIBLE);
                verifyOtpButton.setVisibility(View.VISIBLE);
                Toast.makeText(MobileVerificationActivity.this, "OTP has been sent", Toast.LENGTH_SHORT).show();
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = otpInput.getText().toString().trim();
                String phoneNumber = phoneNumberInput.getText().toString().trim();
                String nic = nicInput.getText().toString().trim();

                if (generatedOtp.equals(enteredOtp)) {
                    // Update database with phone number, NIC, and verification status
                    databaseReference.child("phoneNumber").setValue(phoneNumber);
                    databaseReference.child("nic").setValue(nic);

                    // Send verification email
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MobileVerificationActivity.this, "Verification email sent to " + auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MobileVerificationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    Toast.makeText(MobileVerificationActivity.this, "OTP Verification successful", Toast.LENGTH_SHORT).show();

                    // Redirect to Dashboard or next screen
                    Intent intent = new Intent(MobileVerificationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MobileVerificationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
