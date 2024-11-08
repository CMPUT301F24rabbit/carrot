package com.example.goldencarrot.views;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.FacilityRepository;
import com.example.goldencarrot.data.model.user.FacilityUserImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FacilityProfileActivity extends AppCompatActivity {
    private static final String TAG = "FacilityProfileActivity";

    private EditText nameEditText, locationEditText, descriptionEditText, contactInfoEditText;
    private ImageView facilityImageView;
    private Button saveButton;

    private FacilityRepository facilityRepository;
    private String facilityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_profile);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        contactInfoEditText = findViewById(R.id.contactInfoEditText);
        facilityImageView = findViewById(R.id.facilityImageView);
        saveButton = findViewById(R.id.saveButton);

        // Initialize FacilityRepository
        facilityRepository = new FacilityRepository();

        // Check if facilityID was passed in, if so load the profile for editing
        facilityID = getIntent().getStringExtra("facilityID");
        if (facilityID != null) {
            loadFacilityProfile();
        }

        saveButton.setOnClickListener(view -> saveFacilityProfile());
    }

    private void loadFacilityProfile() {
        facilityRepository.loadFacilityProfile(facilityID, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                FacilityUserImpl profile = task.getResult().toObject(FacilityUserImpl.class);
                if (profile != null) {
                    nameEditText.setText(profile.getName());
                    locationEditText.setText(profile.getLocation().orElse(""));
                    descriptionEditText.setText(profile.getLocation().orElse(""));
                    contactInfoEditText.setText(profile.getPhoneNumber().orElse(""));
                    Picasso.get().load(profile.getImageURL().orElse("")).into(facilityImageView);
                }
            } else {
                Log.e(TAG, "Error loading facility profile", task.getException());
            }
        });
    }

    private void saveFacilityProfile() {
        // Retrieve facility-specific details from UI
        String facilityName = nameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String imageURL = ""; // Add logic to get the image URL if needed

        // Prepare a map of fields to update
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        facilityData.put("location", location);
        facilityData.put("imageURL", imageURL);

        // Get the current user's document reference (assuming user ID is stored in uId)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        // Update the facility fields in the user's document
        userDocRef.update(facilityData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility profile updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating facility profile", e));
    }
}