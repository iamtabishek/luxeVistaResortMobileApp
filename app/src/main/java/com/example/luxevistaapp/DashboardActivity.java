package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class DashboardActivity extends AppCompatActivity {

    private ImageView companyLogo;
    private TextView welcomeText, userNameText;
    private Button profileButton, roomBookingButton, serviceReservationButton, offersButton, logoutButton, bookingsButton, reservationsButton;

    private DatabaseReference verifiedUsersRef, databaseReference;
    private FirebaseAuth auth;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        companyLogo = findViewById(R.id.companyLogo);
        welcomeText = findViewById(R.id.welcomeText);
        userNameText = findViewById(R.id.userNameText);
        profileButton = findViewById(R.id.profileButton);
        roomBookingButton = findViewById(R.id.roomBookingButton);
        serviceReservationButton = findViewById(R.id.serviceReservationButton);
        offersButton = findViewById(R.id.notificationsButton);
        logoutButton = findViewById(R.id.logoutButton);
        bookingsButton = findViewById(R.id.bookingsButton);
        reservationsButton = findViewById(R.id.reservationsButton);


        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();

        // Fetch Logged-In User's Details
        String userID = auth.getCurrentUser().getUid();
        databaseReference.child("VerifiedUsers").child(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String email = task.getResult().child("email").getValue(String.class);
                userEmail = email;

            }

            if (user != null) {
                String userId = user.getUid();
                verifiedUsersRef = FirebaseDatabase.getInstance().getReference("VerifiedUsers").child(userId);

                // Fetch the user's name from VerifiedUsers table
                verifiedUsersRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.getValue(String.class);
                        if (userName != null && !userName.isEmpty()) {
                            userNameText.setText(userName);
                        } else {
                            userNameText.setText("Guest");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DashboardActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                        userNameText.setText("Guest");
                    }
                });
            } else {
                userNameText.setText("Guest");
            }

            // Set up listeners for buttons
            profileButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, UserDetailsActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            });

            roomBookingButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, RoomBookingActivity.class);
                startActivity(intent);
            });

            serviceReservationButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ServiceReservationActivity.class);
                startActivity(intent);
            });

            bookingsButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, MyBookingsActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);

            });

            reservationsButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, MyReservationsActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);

            });

            offersButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, OffersActivity.class);
                startActivity(intent);
            });

            logoutButton.setOnClickListener(v -> {
                Toast.makeText(DashboardActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                auth.signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

        });
    }
}
