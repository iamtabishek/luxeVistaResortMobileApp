package com.example.luxevistaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceReservationActivity extends AppCompatActivity {

    private LinearLayout serviceList;
    private Button backButton;

    private DatabaseReference databaseReference;
    private List<Service> serviceDataList;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reservation);

        // Initialize Views
        serviceList = findViewById(R.id.serviceList);
        backButton = findViewById(R.id.backButton);

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Services");
        serviceDataList = new ArrayList<>();

        // Session Management
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", null);
        if (userEmail == null) {
            // Redirect to Login if session not found
            startActivity(new Intent(ServiceReservationActivity.this, LoginActivity.class));
            finish();
        }

        // Fetch Data from Firebase
        fetchServiceData();



        // Back Button Click Listener
        backButton.setOnClickListener(v -> {
            // Handle back navigation
            finish();
        });
    }

    private void fetchServiceData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serviceDataList.clear();
                // Loop through each Service (Service1, Service2, ...)
                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                    Service service = serviceSnapshot.getValue(Service.class);
                    if (service != null) {
                        serviceDataList.add(service);
                    }
                }
                // Display fetched data
                displayServices(serviceDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log or handle errors
            }
        });
    }

    private void displayServices(List<Service> services) {
        serviceList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Service service : services) {
            View serviceView = inflater.inflate(R.layout.service_item, serviceList, false);

            ImageView serviceImage = serviceView.findViewById(R.id.serviceImage);
            TextView serviceName = serviceView.findViewById(R.id.serviceName);
            TextView serviceDescription = serviceView.findViewById(R.id.serviceDescription);
            TextView servicePrice = serviceView.findViewById(R.id.servicePrice);
            Button serviceReserveButton = serviceView.findViewById(R.id.reserveServiceButton);

            // Set Text Data
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            servicePrice.setText(String.format("LKR %.2f per hour", service.getPrice()));

            // Load Image from URL
            // Inside the for loop of displayRooms
            if (service.getPictureUrl() != null && !service.getPictureUrl().isEmpty()) {
                Glide.with(this)
                        .load(service.getPictureUrl())
                        .error(R.drawable.error_img)             // Add an error image
                        .into(serviceImage);
            } else {
                serviceImage.setImageResource(R.drawable.luxevista_logo); // Fallback image
            }



            // Book Now Button Click Listener
            serviceReserveButton.setOnClickListener(v -> {
                // Handle booking logic here
                Intent intent = new Intent(ServiceReservationActivity.this, ServiceReservationDetailsActivity.class);
                intent.putExtra("serviceName", service.getName());
                intent.putExtra("serviceDescription", service.getDescription());
                intent.putExtra("servicePrice", service.getPrice());
                intent.putExtra("pictureUrl", service.getPictureUrl());
                startActivity(intent);
            });

            serviceList.addView(serviceView);
        }
    }

}
