package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView welcomeMessage;
    private Button profileButton, roomBookingButton, serviceReservationButton, notificationsButton, logoutButton;
    private ImageView companyLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        companyLogo = findViewById(R.id.companyLogo);
        profileButton = findViewById(R.id.profileButton);
        roomBookingButton = findViewById(R.id.roomBookingButton);
        serviceReservationButton = findViewById(R.id.serviceReservationButton);
        notificationsButton = findViewById(R.id.notificationsButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set the welcome message to display the user's email or name (if available)
        String userEmail = mAuth.getCurrentUser().getEmail();
        welcomeMessage.setText("Welcome, " + userEmail + "!");

        // Navigate to Profile Management
        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileManagementActivity.class);
            startActivity(intent);
        });

        // Navigate to Room Booking
        roomBookingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RoomBookingActivity.class);
            startActivity(intent);
        });

        // Navigate to Service Reservation
        serviceReservationButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ServiceReservationActivity.class);
            startActivity(intent);
        });

        // Navigate to Notifications/Promotions
        notificationsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OffersActivity.class);
            startActivity(intent);
        });

        // Logout button - Logs out the user and redirects to LoginActivity
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
