package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileManagementActivity extends AppCompatActivity {

    private Button changeNameButton, changePhoneNumberButton, changeEmailButton, changePasswordButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_managment);

        // Initialize the buttons
        changeNameButton = findViewById(R.id.changeNameButton);
        changePhoneNumberButton = findViewById(R.id.changePhoneNumberButton);
        changeEmailButton = findViewById(R.id.changeEmailButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        backButton = findViewById(R.id.backButton);

        // Set the button click listeners
        changeNameButton.setOnClickListener(view -> {
            // Navigate to the Change Name activity
            Intent intent = new Intent(ProfileManagementActivity.this, ChangeNameActivity.class);
            startActivity(intent);
        });

        changePhoneNumberButton.setOnClickListener(view -> {
            // Navigate to the Change Phone Number activity
            Intent intent = new Intent(ProfileManagementActivity.this, ChangePhoneNumberActivity.class);
            startActivity(intent);
        });

        changeEmailButton.setOnClickListener(view -> {
            // Navigate to the Change Email activity
            Intent intent = new Intent(ProfileManagementActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
        });

        changePasswordButton.setOnClickListener(view -> {
            // Navigate to the Change Password activity
            Intent intent = new Intent(ProfileManagementActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // Back Button: Navigate to the previous activity
        backButton.setOnClickListener(view -> {
            finish(); // Go back to the previous screen
        });
    }
}
