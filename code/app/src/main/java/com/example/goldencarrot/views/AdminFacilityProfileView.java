package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminFacilityProfileView extends AppCompatActivity {

    private TextView nameText, locationText, descriptionText,
    contactInfoText;
    private Button backButton, deleteButton;
    private String organizerId;
    private FirebaseFirestore db;
    private EventRepository eventRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_facility_profile);

        organizerId = getIntent().getStringExtra("userId");

        db = FirebaseFirestore.getInstance();
        eventRepo = new EventRepository();

        nameText = findViewById(R.id.facilityNameText);
        locationText = findViewById(R.id.facilityLocationText);
        descriptionText = findViewById(R.id.facilityDescriptionText);
        contactInfoText = findViewById(R.id.facilityContactInfoText);

        loadFacilityData();

        backButton = findViewById(R.id.adminFacilityBackBtn);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminFacilityProfileView.this, AdminProfileView.class);
            intent.putExtra("currentUserId", organizerId);
            startActivity(intent);
        });
        deleteButton = findViewById(R.id.adminDeleteFacilityBtn);
        deleteButton.setOnClickListener(view -> {
            deleteFacilityProfile();
            deleteFacilityEvents();
            Intent intent = new Intent(AdminFacilityProfileView.this, AdminProfileView.class);
            intent.putExtra("currentUserId", organizerId);
            startActivity(intent);
        });

    }

    private void loadFacilityData() {
        db.collection("users").document(organizerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameText.setText(documentSnapshot.getString("facilityName"));
                        locationText.setText(documentSnapshot.getString("location"));
                        descriptionText.setText(documentSnapshot.getString("description"));
                        contactInfoText.setText(documentSnapshot.getString("contactInfo"));
                    } else {
                        Log.i(TAG, "Failed to get facility profile info");
                    }
                });
    }
    private void deleteFacilityProfile() {
        DocumentReference userSnapshot = db.collection("users").document(organizerId);
        Map<String,Object> facilityProfileDelete = new HashMap<>();
        // remove all facility profile fields
        facilityProfileDelete.put("facilityName", FieldValue.delete());
        facilityProfileDelete.put("location", FieldValue.delete());
        facilityProfileDelete.put("description", FieldValue.delete());
        facilityProfileDelete.put("contactInfo", FieldValue.delete());
        // change organizer to participant
        facilityProfileDelete.put("userType", "PARTICIPANT");

        userSnapshot.update(facilityProfileDelete).addOnSuccessListener(aVoid -> {
            Toast.makeText(AdminFacilityProfileView.this, "Successfully removed facility profile",
                    Toast.LENGTH_SHORT).show();
        });
    }
    private void deleteFacilityEvents() {
        db.collection("events")
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot allEvents = task.getResult();
                        // delete all events created from facility
                        if (allEvents != null && !allEvents.isEmpty()) {
                            for (QueryDocumentSnapshot event : allEvents) {
                                Log.i(TAG, event.getId());
                                Log.i(TAG, "Organizer id: " + event.getString("organizerId"));
                                if (event.getString("organizerId").equals(organizerId)) {
                                    Log.i(TAG, "deleting event: " + event.getId());
                                    eventRepo.deleteEvent(event.getId());
                                }
                            }
                        } else {
                            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
