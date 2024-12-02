package com.example.goldencarrot.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.goldencarrot.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Activity class for managing the facility profile.
 * Allows the user to view, edit, and update facility information such as name, location, contact details, and description.
 * Also enables toggling of geolocation settings and displays the facility location on a map.
 */
public class FacilityProfileActivity extends AppCompatActivity {

    private static final String TAG = "FacilityProfileActivity";

    private EditText nameEditText, locationEditText, descriptionEditText, contactInfoEditText;
    private WebView mapWebView;
    private Button saveButton, backButton;

    private FirebaseFirestore firestore;
    private String userId;

    /**
     * Initializes the activity and sets up UI components.
     * Fetches the user ID from the intent and loads the corresponding facility profile.
     * Sets up listeners for the save and back buttons.
     *
     * @param savedInstanceState Bundle object containing the previous activity's state (if any).
     */
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
        backButton = findViewById(R.id.facilityProfileBackBtn);
        saveButton = findViewById(R.id.saveFacilityButton);

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
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(FacilityProfileActivity.this, OrganizerHomeView.class);
            startActivity(intent);
        });
    }

    /**
     * Loads the facility profile data from Firestore and populates the UI fields.
     * Fetches the facility name, location, description, contact info, and geolocation settings.
     */
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

    /**
     * Saves the updated facility profile data to Firestore.
     * This includes the facility name, location, contact info, description, and geolocation settings.
     */
    private void saveFacilityProfile() {
        String facilityName = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String contactInfo = contactInfoEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Input validation
        if (facilityName.isEmpty()) {
            Toast.makeText(this, "Facility name cannot be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location.isEmpty()) {
            Toast.makeText(this, "Location cannot be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactInfo.isEmpty() || !contactInfo.contains("@")) {
            Toast.makeText(this, "Contact info must contain '@' and cannot be blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        facilityData.put("location", location);
        facilityData.put("contactInfo", contactInfo);
        facilityData.put("description", description);

        // Save to Firestore
        firestore.collection("users").document(userId)
                .update(facilityData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Facility profile updated successfully");
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

                    // Save latitude and longitude
                    saveLocationCoordinates(location);

                    // Close the activity and return to the previous screen
                    Intent intent = new Intent(FacilityProfileActivity.this, OrganizerHomeView.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating facility profile", e);
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Updates the map WebView with the facility location by using the location's latitude and longitude.
     * The map is fetched from OpenStreetMap.
     *
     * @param location The address of the facility.
     */
    private void updateMapWithLocation(String location) {
        mapWebView.getSettings().setJavaScriptEnabled(true);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                String mapUrl = "https://www.openstreetmap.org/?mlat=" + latitude + "&mlon=" + longitude + "#map=18";
                mapWebView.loadUrl(mapUrl);
            } else {
                Toast.makeText(this, "Could not find location on map.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error: " + e.getMessage(), e);
            Toast.makeText(this, "Error fetching map location.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the latitude and longitude coordinates of the location to Firestore.
     * This method is called when the facility profile is updated.
     *
     * @param location The address of the facility to retrieve coordinates for.
     */
    private void saveLocationCoordinates(String location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                Map<String, Object> coordinates = new HashMap<>();
                coordinates.put("latitude", latitude);
                coordinates.put("longitude", longitude);

                firestore.collection("users").document(userId)
                        .update(coordinates)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Coordinates saved: " + latitude + ", " + longitude))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to save coordinates", e));
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error while saving coordinates: " + e.getMessage(), e);
        }
    }

}
