package com.example.luxevistaapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;




public class ServiceReservationDetailsActivity extends AppCompatActivity {

    private ImageView serviceImage;
    private TextView serviceNameText, serviceDescriptionText, servicePriceText, dateText, timeText;
    private Button bookNowButton, backButton;
    private EditText hoursCountEditText;
    private Calendar Calendar;
    private String selectedDate, selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_booking_details);

        // Initialize UI components
        serviceImage = findViewById(R.id.serviceImage);
        serviceNameText = findViewById(R.id.serviceNameText);
        serviceDescriptionText = findViewById(R.id.serviceDescriptionText);
        servicePriceText = findViewById(R.id.servicePriceText);
        hoursCountEditText = findViewById(R.id.hours);

        bookNowButton = findViewById(R.id.reserveNowButton);
        backButton = findViewById(R.id.backButton);

        dateText = findViewById(R.id.Date);
        timeText = findViewById(R.id.Time);
        Calendar = Calendar.getInstance();


        // Retrieve data passed via Intent
        String serviceName = getIntent().getStringExtra("serviceName");
        String serviceDescription = getIntent().getStringExtra("serviceDescription");
        double servicePrice = getIntent().getDoubleExtra("servicePrice", 0.0);
        String pictureUrl = getIntent().getStringExtra("pictureUrl");

        // Display the data
        serviceNameText.setText(serviceName);
        serviceDescriptionText.setText(serviceDescription);
        servicePriceText.setText(String.format("LKR %.2f per hour", servicePrice));

        // Load image
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            new RoomBookingActivity.LoadImageTask(serviceImage).loadImage(pictureUrl);
        } else {
            serviceImage.setImageResource(R.drawable.luxevista_logo); // Placeholder image
        }

        // Date Picker
        dateText.setOnClickListener(v -> showDatePicker(Calendar,dateText));

        // Time Picker
        timeText.setOnClickListener(v -> showTimePicker(Calendar, timeText));


        // Book Now Button Click Listener
        bookNowButton.setOnClickListener(v -> {
            String totalHours = hoursCountEditText.getText().toString();


            // Pass Data to Next Interface
            Intent intent = new Intent(ServiceReservationDetailsActivity.this, ReservationSummaryActivity.class);
            intent.putExtra("totalHours", totalHours);
            intent.putExtra("serviceName", serviceName);
            intent.putExtra("serviceDescription", serviceDescription);
            intent.putExtra("servicePrice", servicePrice);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            startActivity(intent);
        });

        // Back Button Click Listener
        backButton.setOnClickListener(v -> finish());

    }
    private void showDatePicker(Calendar calendar, TextView dateText) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateText.setText(dateFormat.format(calendar.getTime()));
            selectedDate = dateFormat.format(calendar.getTime());

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(Calendar calendar, TextView timeText) {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            // Set the selected time in the Calendar instance
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // Format the selected time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            selectedTime = timeFormat.format(calendar.getTime());

            // Display the time in the TextView
            timeText.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(); // Use 24-hour format
    }




}
