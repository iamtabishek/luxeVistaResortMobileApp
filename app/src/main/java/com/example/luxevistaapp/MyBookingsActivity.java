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

public class MyBookingsActivity extends AppCompatActivity {

    private LinearLayout roomListContainer;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_bookings); // Your XML layout file

        // Initialize UI components
        roomListContainer = findViewById(R.id.RoomList);
        backButton = findViewById(R.id.backButton);

        // Get user email or ID (assuming it's passed via Intent)
        String userEmail = getIntent().getStringExtra("userEmail");


        // Fetch bookings from Firebase
        fetchUserBookings(userEmail);

        // Back button logic
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchUserBookings(String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Bookings"); // Point to your Firebase "Bookings" node

        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            roomListContainer.removeAllViews(); // Clear existing views
                            for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                                // Safely retrieve values, converting Long to String if needed
                                String confirmationID = String.valueOf(bookingSnapshot.child("confirmationID").getValue());
                                String roomName = String.valueOf(bookingSnapshot.child("roomName").getValue());
                                String checkInDate = String.valueOf(bookingSnapshot.child("checkInDate").getValue());
                                String checkOutDate = String.valueOf(bookingSnapshot.child("checkOutDate").getValue());
                                String totalPrice = String.valueOf(bookingSnapshot.child("totalPrice").getValue());

                                // Dynamically add booking details
                                addBookingDetails(confirmationID, roomName, checkInDate, checkOutDate, totalPrice);
                            }
                        } else {
                            Toast.makeText(MyBookingsActivity.this, "No bookings found for this user.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MyBookingsActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void addBookingDetails(String confirmationID, String roomName, String checkInDate, String checkOutDate, String totalPrice) {
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

        // Confirmation ID
        TextView confirmationIDText = new TextView(this);
        confirmationIDText.setText("Confirmation ID: " + confirmationID);
        confirmationIDText.setTextSize(16);

        // Use ContextCompat to get the color (avoid deprecated getColor)
        int primaryColor = ContextCompat.getColor(this, R.color.primary_color);
        confirmationIDText.setTextColor(primaryColor);
        bookingLayout.addView(confirmationIDText);

        // Room Name
        TextView roomNameText = new TextView(this);
        roomNameText.setText("Room Name: " + roomName);
        roomNameText.setTextSize(18);
        roomNameText.setTextColor(primaryColor);
        roomNameText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(roomNameText);

        // Check-in Date
        TextView checkInText = new TextView(this);
        checkInText.setText("Check-in Date: " + checkInDate);
        checkInText.setTextSize(16);
        checkInText.setTextColor(primaryColor);
        checkInText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(checkInText);

        // Check-out Date
        TextView checkOutText = new TextView(this);
        checkOutText.setText("Check-out Date: " + checkOutDate);
        checkOutText.setTextSize(16);
        checkOutText.setTextColor(primaryColor);
        checkOutText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(checkOutText);

        // Total Price
        TextView priceText = new TextView(this);
        priceText.setText("Total Price: " + totalPrice);
        priceText.setTextSize(16);
        priceText.setTextColor(primaryColor);
        priceText.setPadding(0, 8, 0, 0);
        bookingLayout.addView(priceText);

        // Add to main list container
        roomListContainer.addView(bookingLayout);

        // Add a divider between bookings
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        ));
        divider.setBackgroundColor(primaryColor);
        roomListContainer.addView(divider);
    }
}
