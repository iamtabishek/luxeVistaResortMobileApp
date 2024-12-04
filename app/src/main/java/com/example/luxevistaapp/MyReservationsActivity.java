package com.example.luxevistaapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.luxevistaapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyReservationsActivity extends AppCompatActivity {

    private LinearLayout serviceListContainer;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_reservations); // Your XML layout file

        // Initialize UI components
        serviceListContainer = findViewById(R.id.ServiceList);
        backButton = findViewById(R.id.backButton);

        // Get user email or ID (assuming it's passed via Intent)
        String userEmail = getIntent().getStringExtra("userEmail");


        // Fetch services from Firebase
        fetchUserReservations(userEmail);

        // Back button logic
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchUserReservations(String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Reservations"); // Point to your Firebase "Reservations" node

        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            serviceListContainer.removeAllViews(); // Clear existing views
                            for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                                // Safely retrieve values, converting Long to String if needed
                                String reservationID = String.valueOf(bookingSnapshot.child("reservationID").getValue());
                                String serviceName = String.valueOf(bookingSnapshot.child("serviceName").getValue());
                                String date = String.valueOf(bookingSnapshot.child("date").getValue());
                                String time = String.valueOf(bookingSnapshot.child("time").getValue());
                                String totalHours = String.valueOf(bookingSnapshot.child("totalHours").getValue());
                                String totalPrice = String.valueOf(bookingSnapshot.child("totalPrice").getValue());


                                // Dynamically add reservations details
                                addReservationDetails(reservationID, serviceName, date, time, totalHours, totalPrice);
                            }
                        } else {
                            Toast.makeText(MyReservationsActivity.this, "No reservations found for this user.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyReservationsActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void addReservationDetails(String reservationID, String serviceName, String date, String time, String totalHours, String totalPrice) {
        LinearLayout bookingLayout = new LinearLayout(this);
        bookingLayout.setOrientation(LinearLayout.VERTICAL);
        bookingLayout.setPadding(16, 16, 16, 16);

        // Add margins to the booking layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 20, 0, 20); // Add 16dp top and bottom margins
        bookingLayout.setLayoutParams(layoutParams);


        // Use ContextCompat to get the drawable (avoid deprecated getDrawable)
        Drawable roundedBackground = ContextCompat.getDrawable(this, R.drawable.rounded_background);
        bookingLayout.setBackground(roundedBackground);
        bookingLayout.setElevation(4);

        // Reservation ID
        TextView confirmationIDText = new TextView(this);
        confirmationIDText.setText("Reservation ID: " + reservationID);
        confirmationIDText.setTextSize(16);

        // Use ContextCompat to get the color (avoid deprecated getColor)
        int primaryColor = ContextCompat.getColor(this, R.color.primary_color);
        confirmationIDText.setTextColor(primaryColor);
        bookingLayout.addView(confirmationIDText);

        // Service Name
        TextView roomNameText = new TextView(this);
        roomNameText.setText("Service Name: " + serviceName);
        roomNameText.setTextSize(18);
        roomNameText.setTextColor(primaryColor);
        roomNameText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(roomNameText);

        // Date
        TextView dateText = new TextView(this);
        dateText.setText("Date: " + date);
        dateText.setTextSize(16);
        dateText.setTextColor(primaryColor);
        dateText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(dateText);

        // Time
        TextView timeText = new TextView(this);
        timeText.setText("Time: " + time);
        timeText.setTextSize(16);
        timeText.setTextColor(primaryColor);
        timeText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(timeText);

        // Hours
        TextView checkInText = new TextView(this);
        checkInText.setText("Total Hours: " + totalHours);
        checkInText.setTextSize(16);
        checkInText.setTextColor(primaryColor);
        checkInText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(checkInText);

        // Total Price
        TextView priceText = new TextView(this);
        priceText.setText("Total Price: " + totalPrice);
        priceText.setTextSize(16);
        priceText.setTextColor(primaryColor);
        priceText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(priceText);

        // Add to main list container
        serviceListContainer.addView(bookingLayout);

        // Add a divider between reservstions
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        ));
        divider.setBackgroundColor(primaryColor);
        serviceListContainer.addView(divider);
    }
}
