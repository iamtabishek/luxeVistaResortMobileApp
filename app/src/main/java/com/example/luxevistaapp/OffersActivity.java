package com.example.luxevistaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.luxevistaapp.R;

public class OffersActivity extends AppCompatActivity {

    private Button enableNotificationsButton, backToHomeButton;
    private boolean isNotificationsEnabled = false; // Tracks notification state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers); // Replace with your XML layout file name

        // Initialize Views
        initializeViews();

        // Set click listeners
        enableNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNotifications();
            }
        });

        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the current activity and go back
            }
        });
    }

    private void initializeViews() {
        // Initialize views from XML
        enableNotificationsButton = findViewById(R.id.enableNotificationsButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);


    }

    // Function to toggle notifications on/off
    private void toggleNotifications() {
        if (isNotificationsEnabled) {
            // If notifications are enabled, disable them
            isNotificationsEnabled = false;
            enableNotificationsButton.setText("Enable Notifications");
            Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show();
        } else {
            // If notifications are disabled, enable them
            isNotificationsEnabled = true;
            enableNotificationsButton.setText("Disable Notifications");
            Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
