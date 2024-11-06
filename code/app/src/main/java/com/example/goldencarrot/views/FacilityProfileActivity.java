package com.example.goldencarrot.views;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.user.UserFacility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FacilityProfileActivity extends AppCompatActivity {
    private static final String TAG = "FacilityProfileActivity";

    private EditText nameEditText, locationEditText, descriptionEditText, contactInfoEditText;
    private ImageView facilityImageView;
    private Button saveButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String facilityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_profile);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        contactInfoEditText = findViewById(R.id.contactInfoEditText);
        facilityImageView = findViewById(R.id.facilityImageView);
        saveButton = findViewById(R.id.saveButton);

        // Check if facilityID was passed in, if so load the profile for editing
        facilityID = getIntent().getStringExtra("facilityID");
        if (facilityID != null) {
            loadFacilityProfile();
        }

        saveButton.setOnClickListener(view -> saveFacilityProfile());
    }

    private void loadFacilityProfile() {
        DocumentReference docRef = db.collection("facilities").document(facilityID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            UserFacility profile = documentSnapshot.toObject(UserFacility.class);
            if (profile != null) {
                nameEditText.setText(profile.getName());
                locationEditText.setText(profile.getLocation());
                descriptionEditText.setText(profile.getDescription());
                contactInfoEditText.setText(profile.getContactInfo());
                Picasso.get().load(profile.getImageURL()).into(facilityImageView);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading facility profile", e));
    }

    private void saveFacilityProfile() {
        String name = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String contactInfo = contactInfoEditText.getText().toString().trim();
        String imageURL = ""; // Add logic to get URL from an uploaded image

        if (facilityID == null) {
            // Generate a new ID for a new facility
            facilityID = db.collection("facilities").document().getId();
        }

        String organizerID = auth.getCurrentUser().getUid();
        UserFacility profile = new UserFacility(facilityID, organizerID, name, location, description, contactInfo, imageURL);

        // Save profile in Firestore
        db.collection("facilities").document(facilityID)
                .set(profile)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility profile saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving facility profile", e));
    }
}