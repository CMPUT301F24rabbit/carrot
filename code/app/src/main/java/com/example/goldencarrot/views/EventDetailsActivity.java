package com.example.goldencarrot.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private TextView eventDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize TextView
        eventDetailsTextView = findViewById(R.id.eventDetailsTextView);

        // Get the event ID from the Intent
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        listenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Retrieve event details from Firestore
                String eventName = snapshot.getString("eventName");
                String eventDetails = snapshot.getString("eventDetails");
                String location = snapshot.getString("location");
                String date = snapshot.getString("date");

                // Display event details
                eventDetailsTextView.setText(String.format("Event Name: %s\nEvent Details: %s\nLocation: %s\nDate: %s",
                        eventName, eventDetails, location, date));
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
