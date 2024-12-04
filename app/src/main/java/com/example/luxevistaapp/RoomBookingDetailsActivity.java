package com.example.luxevistaapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Date;
import java.util.Locale;

public class RoomBookingDetailsActivity extends AppCompatActivity {

    private ImageView roomImage;
    private TextView roomNameText, roomDescriptionText, roomPriceText;
    private TextView checkInDateTextView, checkOutDateTextView;
    private TextView totalDaysTextView;
    private Button bookNowButton, backButton;
    private Calendar checkInCalendar, checkOutCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_booking_details);

        // Initialize UI components
        roomImage = findViewById(R.id.roomImage);
        roomNameText = findViewById(R.id.roomNameText);
        roomDescriptionText = findViewById(R.id.roomDescriptionText);
        roomPriceText = findViewById(R.id.roomPriceText);

        checkInDateTextView = findViewById(R.id.checkInDate);
        checkOutDateTextView = findViewById(R.id.checkOutDate);
        totalDaysTextView = findViewById(R.id.totalDays);
        bookNowButton = findViewById(R.id.bookNowButton);
        backButton = findViewById(R.id.backButton);

        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();

        // Retrieve data passed via Intent
        String roomName = getIntent().getStringExtra("roomName");
        String roomDescription = getIntent().getStringExtra("roomDescription");
        double roomPrice = getIntent().getDoubleExtra("roomPrice", 0.0);
        String pictureUrl = getIntent().getStringExtra("pictureUrl");

        // Display the data
        roomNameText.setText(roomName);
        roomDescriptionText.setText(roomDescription);
        roomPriceText.setText(String.format("LKR %.2f per day", roomPrice));

        // Load image
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            new RoomBookingActivity.LoadImageTask(roomImage).loadImage(pictureUrl);
        } else {
            roomImage.setImageResource(R.drawable.luxevista_logo); // Placeholder image
        }

        // Check-in Date Picker
        checkInDateTextView.setOnClickListener(v -> showDatePicker(checkInCalendar, checkInDateTextView));

        // Check-out Date Picker
        checkOutDateTextView.setOnClickListener(v -> showDatePicker(checkOutCalendar, checkOutDateTextView));

        // Book Now Button Click Listener
        bookNowButton.setOnClickListener(v -> {
            String checkInDate = checkInDateTextView.getText().toString();
            String checkOutDate = checkOutDateTextView.getText().toString();
            String totalDays = totalDaysTextView.getText().toString().replace("Total Days: ", "").trim();

            // Pass Data to Next Interface
            Intent intent = new Intent(RoomBookingDetailsActivity.this, BookingSummaryActivity.class);
            intent.putExtra("checkInDate", checkInDate);
            intent.putExtra("checkOutDate", checkOutDate);
            intent.putExtra("totalDays", totalDays);
            intent.putExtra("roomName", roomName);
            intent.putExtra("roomDescription", roomDescription);
            intent.putExtra("roomPrice", roomPrice);
            startActivity(intent);
        });

        // Back Button Click Listener
        backButton.setOnClickListener(v -> finish());
    }

    private void showDatePicker(Calendar calendar, TextView dateTextView) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateTextView.setText(dateFormat.format(calendar.getTime()));
            calculateTotalDays();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void calculateTotalDays() {
        String checkInDate = checkInDateTextView.getText().toString();
        String checkOutDate = checkOutDateTextView.getText().toString();

        if (!checkInDate.isEmpty() && !checkOutDate.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date startDate = dateFormat.parse(checkInDate);
                Date endDate = dateFormat.parse(checkOutDate);

                if (startDate != null && endDate != null && endDate.after(startDate)) {
                    long diffInMillis = endDate.getTime() - startDate.getTime();
                    long totalDays = (diffInMillis / (1000 * 60 * 60 * 24))+1; // Convert milliseconds to days
                    totalDaysTextView.setText("Total Days: " + totalDays);
                } else {
                    long totalDays = 1;
                    totalDaysTextView.setText("Total Days: " + totalDays);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
