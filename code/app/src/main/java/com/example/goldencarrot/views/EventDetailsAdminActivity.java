package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EventDetailsAdminActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EventRepository eventRepository;
    private WaitListRepository waitListRepository;
    private ListenerRegistration listenerRegistration;
    private TextView eventNameTitleView, eventDateView, eventLocationView, eventTimeView, eventDetailsView;
    private ImageView eventPosterView;
    private Button backButton, deleteEventButton;
    private String waitlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        waitListRepository = new WaitListRepository();

        // Initialize Views
        eventPosterView = findViewById(R.id.event_DetailPosterView);
        eventNameTitleView = findViewById(R.id.event_DetailNameTitleView);
        eventDateView = findViewById(R.id.event_DetailDateView);
        eventLocationView = findViewById(R.id.event_DetailLocationView);
        eventTimeView = findViewById(R.id.event_DetailTimeView);
        eventDetailsView = findViewById(R.id.event_DetailDetailsView);
        backButton = findViewById(R.id.back_DetailButton);
        deleteEventButton = findViewById(R.id.delete_DetailEventBtn);

        // Get the event ID from the Intent
        String eventId = getIntent().getStringExtra("eventId");

        if (eventId != null) {
            // Load event details from Firestore using the event ID
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
        }

        // Set up back button to navigate back to BrowseEventsActivity
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailsAdminActivity.this, BrowseEventsActivity.class);
            startActivity(intent);
        });

        // Set up delete event button
        deleteEventButton.setOnClickListener(view -> deleteEvent(eventId));
    }

    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        listenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("EventDetailsAdmin", "Error fetching event details", e);
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                // Retrieve event details from Firestore
                String eventName = snapshot.getString("eventName");
                String eventDetails = snapshot.getString("eventDetails");
                String location = snapshot.getString("location");
                String date = snapshot.getString("date");
                String time = snapshot.getString("time");

                // Display event details
                eventNameTitleView.setText(eventName);
                eventDateView.setText(date);
                eventLocationView.setText(location);
                eventTimeView.setText(time);
                eventDetailsView.setText(eventDetails);
                // If there is an image URL in the document, you can use it to load an image with a library like Picasso or Glide
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEvent(String eventId) {
        if (eventId != null) {
            // delete waitlist of the event
            eventRepository.getBasicEventById(eventId, new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    waitlistId = event.getWaitListId();
                    waitListRepository.deleteWaitList(waitlistId);
                    Log.d("EventDetailsAdminActivity", "waitlist id: " + waitlistId);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("EventDetailAdminActivity", "failed to get event");
                }
            });
            firestore.collection("events").document(eventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, BrowseEventsActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show());
            Intent intent = new Intent(EventDetailsAdminActivity.this, BrowseEventsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
