package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Organizer's view of an event's details
 */
public class OrganizerEventDetailsActivity extends AppCompatActivity {

    // Initialize Firestore and Event Repository
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private EventRepository eventRepository;
    private String deviceID;

    // UI initialize
    private ImageView eventPosterView;
    private TextView eventNameTextView;
    private TextView eventDateTextView;
    private TextView eventLocationTextView;
    private TextView eventTimeTextView;
    private TextView eventDetailsTextView;
    private PopupWindow entrantsPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Firestore initializaation
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();

        // Get eventID from Intent
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
        }
        // hide delete button

        // UI Initialization
        eventPosterView = findViewById(R.id.event_DetailPosterView);
        eventNameTextView = findViewById(R.id.event_DetailNameTitleView);
        eventDateTextView = findViewById(R.id.event_DetailDateView);
        eventLocationTextView = findViewById(R.id.event_DetailLocationView);
        eventTimeTextView = findViewById(R.id.event_DetailTimeView);
        eventDetailsTextView = findViewById(R.id.event_DetailDetailsView);

        deviceID = getDeviceId(this);

        // Set up back button
        Button backButton = findViewById(R.id.back_DetailButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerHomeView.class);
                startActivity(intent);
            }
        });

        // Does organizer have delete permissions for their events? Yes right?

        Button deleteEventBtn = findViewById(R.id.delete_DetailEventBtn);
        deleteEventBtn.setVisibility(View.INVISIBLE);
        /*
        deleteEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventRepository.deleteEvent(eventId);
                Toast.makeText(OrganizerEventDetailsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerHomeView.class);
                startActivity(intent);
            }
        });
         */

        // Entrants button
        Button entrantsButton = findViewById(R.id.button_DetailViewEventLists);
        entrantsButton.setOnClickListener(v -> showEntrantsPopup());
    }

    /**
     * Retrieves the Android device ID.
     *
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Load Event details from Firestore, making sure to match with organizers device
     *
     * @param eventId ID of the event to load.
     */
    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        listenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // is event associated with organizers deviceId?
                String organizerId = snapshot.getString("organizerId");

                if (organizerId != null && organizerId.equals(deviceID)) {
                    // Then the Organizer matches, so we can load event details
                    String eventName = snapshot.getString("eventName");
                    String eventDetails = snapshot.getString("eventDetails");
                    String location = snapshot.getString("location");
                    String date = snapshot.getString("date");
                    String time = snapshot.getString("time");
                    int posterResId = snapshot.getLong("posterResId") != null ? snapshot.getLong("posterResId").intValue() : R.drawable.default_poster;

                    // Now display the details
                    eventNameTextView.setText(eventName);
                    eventDateTextView.setText("Date: " + date);
                    eventLocationTextView.setText("Location: " + location);
                    eventTimeTextView.setText("Time: " + time);
                    eventDetailsTextView.setText(eventDetails);
                    eventPosterView.setImageResource(posterResId);


                } else {
                    // Organizer ID does not match, access is denied
                    Toast.makeText(this, "Access denied: you aren ot authorized to view this event", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Show a popup with Entrants options (Waitlisted, Accepted, Declined)
     */
    private void showEntrantsPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_event_lists, null);
        entrantsPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        entrantsPopup.showAtLocation(findViewById(R.id.button_DetailViewEventLists), Gravity.CENTER, 0, 0);

        Button waitlistedButton = popupView.findViewById(R.id.button_EventDetailWaitlistedEntrants);
        Button acceptedButton = popupView.findViewById(R.id.button_EventDetailAcceptedEntrants);
        Button declinedButton = popupView.findViewById(R.id.button_EventDetailRejectedEntrants);

        waitlistedButton.setOnClickListener(v -> openEntrantsView("waitlisted"));
        acceptedButton.setOnClickListener(v -> openEntrantsView("accepted"));
        declinedButton.setOnClickListener(v -> openEntrantsView("declined"));
    }

    /**
     * Opens the OrganizerWaitlistView with the specified entrant status.
     *
     * @param status The entrant status to pass to the view ("waitlisted", "accepted", "declined").
     */
    private void openEntrantsView(String status) {
        Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerWaitlistView.class);
        intent.putExtra("entrantStatus", status); // Pass the status to OrganizerWaitlistView
        entrantsPopup.dismiss();
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
