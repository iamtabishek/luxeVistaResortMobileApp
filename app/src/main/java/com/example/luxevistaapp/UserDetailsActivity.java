package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailsActivity extends AppCompatActivity {

    // Declare Views
    private TextView nameTextView, phoneTextView, emailTextView, nicTextView;
    private Button backButton, editProfileButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview); // Ensure this matches your XML layout file name

        // Initialize Views
        nameTextView = findViewById(R.id.nameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        nicTextView = findViewById(R.id.NICTextView);

        backButton = findViewById(R.id.backButton);
        editProfileButton = findViewById(R.id.editProfileButton);

        // Get email from the intent
        String userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "No email provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("VerifiedUsers");

        // Fetch user details from the database
        fetchUserDetails(userEmail);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Edit Profile button functionality
        editProfileButton.setOnClickListener(v -> {
            // Navigate to the EditProfileActivity (Create this activity if not already created)
            Intent intent = new Intent(UserDetailsActivity.this, ProfileManagementActivity.class);
            intent.putExtra("email", userEmail); // Pass email to the next activity
            startActivity(intent);
        });
    }

    private void fetchUserDetails(String email) {
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String name = userSnapshot.child("name").getValue(String.class);
                                String phone = userSnapshot.child("phoneNumber").getValue(String.class);
                                String userEmail = userSnapshot.child("email").getValue(String.class);
                                String nic = userSnapshot.child("nic").getValue(String.class);

                                nameTextView.setText(name != null ? name : "N/A");
                                phoneTextView.setText(phone != null ? phone : "N/A");
                                emailTextView.setText(userEmail != null ? userEmail : "N/A");
                                nicTextView.setText(nic != null ? nic : "N/A");
                            }
                        } else {
                            Toast.makeText(UserDetailsActivity.this, "User not found in database!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserDetailsActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
