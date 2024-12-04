package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button sendResetEmailButton, backButton;
    private TextView invalidEmailTextView;
    private FirebaseAuth auth;
    private DatabaseReference verifiedUsersRef;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword_mailverification);

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        verifiedUsersRef = FirebaseDatabase.getInstance().getReference("VerifiedUsers");

        // Initialize views
        sendResetEmailButton = findViewById(R.id.sendResetEmailButton1);
        backButton = findViewById(R.id.backButton);
        invalidEmailTextView = findViewById(R.id.tv_invalid_email);

        // Fetch email from VerifiedUsers table
        if (currentUser != null) {
            fetchUserEmail(currentUser.getUid());
        }

        // Set listener for Send Password Reset Email button
        sendResetEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });

        // Set listener for Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchUserEmail(String userId) {
        verifiedUsersRef.child(userId).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userEmail = snapshot.getValue(String.class);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "User email not found in database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResetPasswordActivity.this, "Failed to retrieve email: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPasswordResetEmail() {
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "User email is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(ResetPasswordActivity.this, "Reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();

                    } else {
                        invalidEmailTextView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
