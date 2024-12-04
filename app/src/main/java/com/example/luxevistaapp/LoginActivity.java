package com.example.luxevistaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView forgotPassword, tvSignupRedirect;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        tvSignupRedirect = findViewById(R.id.tv_signup_redirect);

        // Set up the login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Forgot password click event
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ForgotPasswordActivity
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Redirect to sign-up page
        tvSignupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", String.valueOf(emailInput));
                        editor.apply();

                        FirebaseUser user = auth.getCurrentUser();

                        if (user.isEmailVerified()) {
                            String userId = user.getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            DatabaseReference verifiedUserRef = FirebaseDatabase.getInstance().getReference("VerifiedUsers").child(userId);

                            // Check if the UID already exists in "VerifiedUsers"
                            verifiedUserRef.get().addOnCompleteListener(verifiedCheckTask -> {
                                if (verifiedCheckTask.isSuccessful() && !verifiedCheckTask.getResult().exists()) {
                                    // UID does not exist in "VerifiedUsers", so copy details from "Users"
                                    userRef.get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (DataSnapshot data : task1.getResult().getChildren()) {
                                                verifiedUserRef.child(data.getKey()).setValue(data.getValue());
                                            }
                                        } else {
                                            Toast.makeText(this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            // Retrieve user's name to navigate to the dashboard
                            userRef.child("name").get().addOnCompleteListener(task3 -> {
                                if (task3.isSuccessful()) {
                                    String userName = task3.getResult().getValue(String.class);
                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                    intent.putExtra("USER_NAME", userName);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Failed to retrieve user name.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                            user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}