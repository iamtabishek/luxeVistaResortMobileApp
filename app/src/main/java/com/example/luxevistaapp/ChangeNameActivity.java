package com.example.luxevistaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeNameActivity extends AppCompatActivity {

    private static final String TAG = "ChangeNameActivity";
    private EditText nameEditText;
    private Button saveNameButton, backButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_change);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        saveNameButton = findViewById(R.id.saveNameButton);
        backButton = findViewById(R.id.backButton);

        // Set click listener for Save Changes button
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString().trim();

                if (newName.isEmpty()) {
                    Toast.makeText(ChangeNameActivity.this, "Please enter a new name", Toast.LENGTH_SHORT).show();
                } else {
                    // Save the new name to Realtime Database
                    saveNameToDatabase(newName);
                    Intent intent = new Intent(ChangeNameActivity.this, DashboardActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Set click listener for Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Close this activity and go back to the previous screen
            }
        });
    }

    private void saveNameToDatabase(String newName) {
        // Ensure the user is authenticated
        if (auth.getCurrentUser() == null) {
            Toast.makeText(ChangeNameActivity.this, "User is not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User is not authenticated.");
            return;
        }

        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "User ID: " + userId);

        // Reference to the user's data in the Realtime Database
        DatabaseReference userRef = databaseReference.child("VerifiedUsers").child(userId).child("name");

        // Update the "name" field in the Realtime Database
        userRef.setValue(newName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Name updated successfully in Realtime Database.");
                        Toast.makeText(ChangeNameActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update name: " + e.getMessage(), e);
                        Toast.makeText(ChangeNameActivity.this, "Failed to update name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
