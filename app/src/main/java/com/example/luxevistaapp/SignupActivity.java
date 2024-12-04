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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText nameInput, signupEmailInput, signupPasswordInput, signupConfirmPasswordInput;
    private Button signupButton;
    private TextView tvLoginRedirect;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        signupEmailInput = findViewById(R.id.signupEmailInput);
        signupPasswordInput = findViewById(R.id.signupPasswordInput);
        signupConfirmPasswordInput = findViewById(R.id.signupConfirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        tvLoginRedirect = findViewById(R.id.tv_login_redirect);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        // Set up the sign-up button listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Redirect to Login Page
        tvLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = signupEmailInput.getText().toString().trim();
        String password = signupPasswordInput.getText().toString().trim();
        String confirmPassword = signupConfirmPasswordInput.getText().toString().trim();

        // Check if any fields are empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password complexity
        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password must be at least 7 characters long, include one uppercase letter, one number, and one symbol.", Toast.LENGTH_LONG).show();
            return;
        }


        // Create user in Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();

                            AppUser userData = new AppUser(name, email); // `false` indicating unverified
                            databaseReference.child(userId).setValue(userData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Intent intent = new Intent(SignupActivity.this, MobileVerificationActivity.class);
                                            intent.putExtra("userId", userId);  // Pass userId to verification activity
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Failed to store user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    // Method to validate password complexity
    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{7,}$";
        return password.matches(passwordPattern);
    }
}