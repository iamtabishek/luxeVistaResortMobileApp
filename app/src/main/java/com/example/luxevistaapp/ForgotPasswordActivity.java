package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetPasswordButton, backToLoginButton;
    private FirebaseAuth auth;
    private DatabaseReference verifiedUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        verifiedUsersRef = FirebaseDatabase.getInstance().getReference("VerifiedUsers");

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        // Set listener for Reset Password button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the email exists in VerifiedUsers table
                checkVerifiedUsersTable(email);
            }
        });

        // Set listener for Back to Login button
        backToLoginButton.setOnClickListener(v -> finish());
    }

    private void checkVerifiedUsersTable(String email) {
        verifiedUsersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Email exists in VerifiedUsers table, send password reset email
                    sendPasswordResetEmail(email);
                } else {
                    // Email does not exist in VerifiedUsers table
                    Toast.makeText(ForgotPasswordActivity.this, "Email not found in our records.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPasswordActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
