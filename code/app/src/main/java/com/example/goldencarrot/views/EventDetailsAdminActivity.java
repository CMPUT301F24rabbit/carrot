package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Admin view of an event's details, with functionality to delete event.
 */
public class EventDetailsAdminActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private TextView eventDetailsTextView;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize Firestore and EventRepository
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();

        // Set up back button
        Button backButton = findViewById(R.id.back_DetailButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize TextView
        ImageView eventPosterView = findViewById(R.id.event_DetailPosterView);
        TextView eventNameTitleView = findViewById(R.id.event_DetailNameTitleView);
        TextView eventDateView = findViewById(R.id.event_DetailDateView);
        TextView eventLocationView = findViewById(R.id.event_DetailLocationView);
        TextView eventTimeView = findViewById(R.id.event_DetailTimeView);
        TextView eventDetailsView = findViewById(R.id.event_DetailDetailsView);

        // Get the event ID from the Intent
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
        }
        // Set up delete event button
        findViewById(R.id.delete_DetailEventBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventRepository.deleteEvent(eventId);
                Intent intent = new Intent(EventDetailsAdminActivity.this, BrowseEventsActivity.class);
                startActivity(intent);
            }
        });
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
