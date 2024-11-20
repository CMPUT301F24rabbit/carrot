package com.example.goldencarrot.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.FacilityRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FacilityProfileActivity extends AppCompatActivity {
    private static final String TAG = "FacilityProfileActivity";

    private EditText nameEditText, locationEditText, descriptionEditText, contactInfoEditText;
    private ImageView facilityImageView;
    private Button saveButton;

    private FacilityRepository facilityRepository;
    private String userId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_profile);

        // Initialize Firestore and Repository
        firestore = FirebaseFirestore.getInstance();
        facilityRepository = new FacilityRepository();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        contactInfoEditText = findViewById(R.id.contactInfoEditText);
        facilityImageView = findViewById(R.id.facilityImageView);
        saveButton = findViewById(R.id.saveButton);

        // Get userId from Intent
        userId = getIntent().getStringExtra("userId");
        Log.d(TAG, "Received userId: " + userId);

        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "userId is null or empty");
            Toast.makeText(this, "Failed to load profile. User ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load facility profile
        loadFacilityProfile();

        saveButton.setOnClickListener(view -> saveFacilityProfile());
    }

    private void loadFacilityProfile() {
        DocumentReference docRef = firestore.collection("users").document(userId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        String facilityName = documentSnapshot.getString("facilityName");
                        String location = documentSnapshot.getString("location");
                        String contactInfo = documentSnapshot.getString("contactInfo");
                        String description = documentSnapshot.getString("description");

                        nameEditText.setText(facilityName != null ? facilityName : "");
                        locationEditText.setText(location != null ? location : "");
                        contactInfoEditText.setText(contactInfo != null ? contactInfo : "");
                        descriptionEditText.setText(description != null ? description : "");
                    } else {
                        Log.e(TAG, "No such document");
                        Toast.makeText(this, "Facility profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch document", e);
                    Toast.makeText(this, "Failed to load facility profile.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveFacilityProfile() {
        String facilityName = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String contactInfo = contactInfoEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        facilityData.put("location", location);
        facilityData.put("contactInfo", contactInfo);
        facilityData.put("description", description);

        firestore.collection("users").document(userId)
                .update(facilityData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Facility profile updated successfully");
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating facility profile", e);
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                });
    }
}
