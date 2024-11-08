package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
/**
 * Activity that displays the details of an event for the admin user.
 * This activity allows the admin to view event details and delete the event,
 * including removing its associated waitlist.
 */
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

        // Initialize Firestore and repositories
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

        // Set up delete event button to remove event and its waitlist
        deleteEventButton.setOnClickListener(view -> deleteEvent(eventId));
    }

    /**
     * Loads the event details from Firestore and displays them on the UI.
     *
     * @param eventId The ID of the event to load.
     */
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

                // Display event details on the UI
                eventNameTitleView.setText(eventName);
                eventDateView.setText(date);
                eventLocationView.setText(location);
                eventTimeView.setText(time);
                eventDetailsView.setText(eventDetails);
                // Optionally, load an image for the event poster if available
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Deletes the event and its associated waitlist from Firestore.
     *
     * @param eventId The ID of the event to delete.
     */
    private void deleteEvent(String eventId) {
        if (eventId != null) {
            // Delete the associated waitlist before deleting the event
            eventRepository.getBasicEventById(eventId, new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    waitlistId = event.getWaitListId();
                    waitListRepository.deleteWaitList(waitlistId);
                    Log.d("EventDetailsAdminActivity", "waitlist id: " + waitlistId);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("EventDetailAdminActivity", "Failed to get event");
                }
            });

            // Delete the event from Firestore
            firestore.collection("events").document(eventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, BrowseEventsActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show());

            // Navigate back to the BrowseEventsActivity
            Intent intent = new Intent(EventDetailsAdminActivity.this, BrowseEventsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Removes the Firestore listener when the activity is stopped to prevent memory leaks.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
