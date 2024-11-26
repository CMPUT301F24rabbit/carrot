package com.example.goldencarrot.views;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityProfileActivity extends AppCompatActivity {
    private static final String TAG = "FacilityProfileActivity";

    private EditText nameEditText, locationEditText, descriptionEditText, contactInfoEditText;
    private WebView mapWebView;
    private Button saveButton;
    private Switch geolocationSwitch;

    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_profile);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        contactInfoEditText = findViewById(R.id.contactInfoEditText);
        mapWebView = findViewById(R.id.mapWebView);
        saveButton = findViewById(R.id.saveButton);
        geolocationSwitch = findViewById(R.id.geolocationSwitch);

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

        // Save button functionality
        saveButton.setOnClickListener(view -> saveFacilityProfile());

        // Handle geolocation toggle
        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateGeolocationRequirement(isChecked));
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
                        Boolean isGeolocationEnabled = documentSnapshot.getBoolean("isGeolocationEnabled");

                        nameEditText.setText(facilityName != null ? facilityName : "");
                        locationEditText.setText(location != null ? location : "");
                        contactInfoEditText.setText(contactInfo != null ? contactInfo : "");
                        descriptionEditText.setText(description != null ? description : "");
                        geolocationSwitch.setChecked(isGeolocationEnabled != null && isGeolocationEnabled);

                        if (location != null && !location.isEmpty()) {
                            updateMapWithLocation(location);
                        }
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

        Geocoder geocoder = new Geocoder(this);
        try {
            // Fetch the latitude and longitude for the entered location
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();

                // Prepare data to save to Firestore
                Map<String, Object> facilityData = new HashMap<>();
                facilityData.put("facilityName", facilityName);
                facilityData.put("location", location);
                facilityData.put("contactInfo", contactInfo);
                facilityData.put("description", description);
                facilityData.put("latitude", latitude);
                facilityData.put("longitude", longitude);

                // Save to Firestore
                firestore.collection("users").document(userId)
                        .update(facilityData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Facility profile updated successfully");
                            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

                            // Update map with the new location
                            updateMap(latitude, longitude);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating facility profile", e);
                            Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Invalid location. Please enter a valid address.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
            Toast.makeText(this, "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGeolocationRequirement(boolean isEnabled) {
        firestore.collection("users").document(userId)
                .update("isGeolocationEnabled", isEnabled)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Geolocation requirement updated");
                    Toast.makeText(this, "Geolocation requirement updated.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update geolocation setting", e);
                    Toast.makeText(this, "Failed to update geolocation requirement.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateMapWithLocation(String location) {
        Geocoder geocoder = new Geocoder(this);
        try {
            // Fetch the latitude and longitude from the address
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();

                // Update the map
                updateMap(latitude, longitude);
            } else {
                Toast.makeText(this, "Unable to fetch location coordinates.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
            Toast.makeText(this, "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMap(double latitude, double longitude) {
        String mapUrl = "https://www.openstreetmap.org/?mlat=" + latitude + "&mlon=" + longitude + "#map=18/" + latitude + "/" + longitude;
        mapWebView.getSettings().setJavaScriptEnabled(true);
        mapWebView.loadUrl(mapUrl);
    }
}
