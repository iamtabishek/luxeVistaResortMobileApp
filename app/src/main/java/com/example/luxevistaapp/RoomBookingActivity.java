package com.example.luxevistaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RoomBookingActivity extends AppCompatActivity {

    private LinearLayout roomList;
    private Spinner roomTypeSpinner, priceRangeSpinner;
    private Button sortButton, backButton;

    private DatabaseReference databaseReference;
    private List<Room> roomDataList;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserSession";

    private void filterAndSortRooms() {
        String selectedRoomType = roomTypeSpinner.getSelectedItem().toString();
        String selectedPriceRange = priceRangeSpinner.getSelectedItem().toString();

        List<Room> filteredList = new ArrayList<>(roomDataList);

        // Filter by Room Type
        if (!selectedRoomType.equalsIgnoreCase("All")) {
            filteredList.removeIf(room -> !room.getName().equalsIgnoreCase(selectedRoomType));
        }

        // Filter by Price Range
        switch (selectedPriceRange) {
            case "Below LKR5000":
                filteredList.removeIf(room -> room.getPrice() >= 5000);
                break;
            case "LKR5000 - LKR10000":
                filteredList.removeIf(room -> room.getPrice() < 5000 || room.getPrice() > 10000);
                break;
            case "Above LKR10000":
                filteredList.removeIf(room -> room.getPrice() <= 10000);
                break;
        }

        // Sort by Price if a sort option is selected
        String selectedSort = roomTypeSpinner.getSelectedItem().toString();
        if (selectedSort.equalsIgnoreCase("Price: Low to High")) {
            Collections.sort(filteredList, Comparator.comparingDouble(Room::getPrice));
        } else if (selectedSort.equalsIgnoreCase("Price: High to Low")) {
            Collections.sort(filteredList, (room1, room2) -> Double.compare(room2.getPrice(), room1.getPrice()));
        }

        // Update UI
        displayRooms(filteredList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);

        // Initialize Views
        roomList = findViewById(R.id.roomList);
        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        priceRangeSpinner = findViewById(R.id.priceRangeSpinner);
        sortButton = findViewById(R.id.sortButton);
        backButton = findViewById(R.id.backButton);


        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
        roomDataList = new ArrayList<>();

        // Session Management
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", null);
        if (userEmail == null) {
            // Redirect to Login if session not found
            startActivity(new Intent(RoomBookingActivity.this, LoginActivity.class));
            finish();
        }

        // Fetch Data from Firebase
        fetchRoomData();

        // Sort Button Click Listener
        sortButton.setOnClickListener(v -> filterAndSortRooms());

        // Back Button Click Listener
        backButton.setOnClickListener(v -> {
            // Handle back navigation
            finish();
        });
    }

    private void fetchRoomData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomDataList.clear();
                // Loop through each Room (Room1, Room2, ...)
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        roomDataList.add(room);
                    }
                }
                // Display fetched data
                displayRooms(roomDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log or handle errors
            }
        });
    }

    private void displayRooms(List<Room> rooms) {
        roomList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Room room : rooms) {
            View roomView = inflater.inflate(R.layout.room_item, roomList, false);

            ImageView roomImage = roomView.findViewById(R.id.roomImage);
            TextView roomDescription = roomView.findViewById(R.id.roomDescription);
            TextView roomDetails = roomView.findViewById(R.id.roomDetails);
            TextView roomPrice = roomView.findViewById(R.id.roomPrice);
            Button bookRoomButton = roomView.findViewById(R.id.bookRoomButton);

            // Set Text Data
            roomDescription.setText(room.getName());
            roomDetails.setText(room.getDescription());
            roomPrice.setText(String.format("LKR %.2f per night", room.getPrice()));

            // Load Image from URL
            // Inside the for loop of displayRooms
            if (room.getPictureUrl() != null && !room.getPictureUrl().isEmpty()) {
                Glide.with(this)
                        .load(room.getPictureUrl())
                        .error(R.drawable.error_img)             // Add an error image
                        .into(roomImage);
            } else {
                roomImage.setImageResource(R.drawable.luxevista_logo); // Fallback image
            }

            // Book Now Button Click Listener
            bookRoomButton.setOnClickListener(v -> {
                // Handle booking logic here
                Intent intent = new Intent(RoomBookingActivity.this, RoomBookingDetailsActivity.class);
                intent.putExtra("roomName", room.getName());
                intent.putExtra("roomDescription", room.getDescription());
                intent.putExtra("roomPrice", room.getPrice());
                intent.putExtra("pictureUrl", room.getPictureUrl());
                startActivity(intent);
            });

            roomList.addView(roomView);
        }
    }



    private void sortRooms() {
        String selectedSort = roomTypeSpinner.getSelectedItem().toString();
        if (selectedSort.equalsIgnoreCase("Price: Low to High")) {
            Collections.sort(roomDataList, Comparator.comparingDouble(Room::getPrice));
        } else if (selectedSort.equalsIgnoreCase("Price: High to Low")) {
            Collections.sort(roomDataList, (room1, room2) -> Double.compare(room2.getPrice(), room1.getPrice()));
        }
    }



    static class LoadImageTask {
        private final ImageView imageView;
        private final ExecutorService executor;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
            this.executor = Executors.newSingleThreadExecutor(); // Executor for background thread
        }

        public void loadImage(String url) {
            Future<?> future = executor.submit(() -> {
                try {
                    InputStream inputStream = new java.net.URL(url).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    // Set the image on the UI thread
                    imageView.post(() -> imageView.setImageBitmap(bitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


    }

}
