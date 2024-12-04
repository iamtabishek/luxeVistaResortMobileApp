package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText currentEmailInput, newEmailInput, confirmNewEmailInput, passwordInput;
    private Button changeEmailButton, backButton;
    private TextView invalidEmailTextView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        currentEmailInput = findViewById(R.id.currentEmailInput);
        newEmailInput = findViewById(R.id.newEmailInput);
        confirmNewEmailInput = findViewById(R.id.confirmNewEmailInput);
        passwordInput = findViewById(R.id.passwordInput);
        changeEmailButton = findViewById(R.id.changeEmailButton);
        backButton = findViewById(R.id.backButton);
        invalidEmailTextView = findViewById(R.id.tv_invalid_email);

        invalidEmailTextView.setVisibility(View.INVISIBLE);

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentEmail = currentEmailInput.getText().toString().trim();
                String newEmail = newEmailInput.getText().toString().trim();
                String confirmNewEmail = confirmNewEmailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(currentEmail) || TextUtils.isEmpty(newEmail) ||
                        TextUtils.isEmpty(confirmNewEmail) || TextUtils.isEmpty(password)) {
                    Toast.makeText(ChangeEmailActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(currentEmail) || !isValidEmail(newEmail)) {
                    Toast.makeText(ChangeEmailActivity.this, "Please enter valid email addresses", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newEmail.equals(confirmNewEmail)) {
                    Toast.makeText(ChangeEmailActivity.this, "New email and confirm email do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                reAuthenticateAndChangeEmail(currentEmail, newEmail, password);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void reAuthenticateAndChangeEmail(final String currentEmail, final String newEmail, String password) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(ChangeEmailActivity.this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.verifyBeforeUpdateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Send verification email to the new address
                        sendVerificationEmail(user);

                        String userId = user.getUid();
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("VerifiedUsers").child(userId);
                        databaseRef.child("email").setValue(newEmail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangeEmailActivity.this, "Email updated in database successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ChangeEmailActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        handleFailure("Failed to update email in database: " + e.getMessage());
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleFailure("Failed to update email: " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleFailure("Re-authentication failed: " + e.getMessage());
            }
        });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangeEmailActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangeEmailActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleFailure(String message) {
        invalidEmailTextView.setVisibility(View.VISIBLE);
        Toast.makeText(ChangeEmailActivity.this, message, Toast.LENGTH_SHORT).show();
        Log.e("ChangeEmailError", message);
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
