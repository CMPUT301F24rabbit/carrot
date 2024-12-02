package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.RanBackground;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

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
    private TextView eventNameTitleView, eventDateView, eventLocationView, eventTimeView, eventDetailsView,
            facilityNameTextView, facilityContactInfoTextView;;
    private ImageView eventPosterView;
    private Button backButton, deleteEventButton;
    private String waitlistId;
    private String deviceID;
    private String eventId;
    private Button generateQRCodeButton;
    private ImageView qrCodeImageView;
    private Button deleteQRCodeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize Firestore and repositories
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        waitListRepository = new WaitListRepository();

        // Apply RNG Background
        RelativeLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));


        // Initialize Views
        eventPosterView = findViewById(R.id.eventPosterImageView);
        eventNameTitleView = findViewById(R.id.event_DetailNameTitleView);
        eventDateView = findViewById(R.id.event_DetailDateView);
        eventLocationView = findViewById(R.id.event_DetailLocationView);
        eventDetailsView = findViewById(R.id.event_DetailDetailsView);
        facilityNameTextView = findViewById(R.id.event_DetailFacilityName);
        facilityContactInfoTextView = findViewById(R.id.event_DetailContactInfo);
        backButton = findViewById(R.id.back_DetailButton);
        deleteEventButton = findViewById(R.id.delete_DetailEventBtn);
        generateQRCodeButton = findViewById(R.id.generateQRCodeButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        deleteQRCodeButton = findViewById(R.id.deleteQRCodeButton);

        // Get the event ID from the Intent
        String eventId = getIntent().getStringExtra("eventId");

        if (eventId != null) {
            // Load event details from Firestore using the event ID
            loadEventDetails(eventId);
            // Load the QR code details and display if they exist
            loadQRCode(eventId);
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

        // Hide Generate QR code button, and show delete button
        generateQRCodeButton.setVisibility(View.GONE);
        deleteQRCodeButton.setVisibility(View.VISIBLE);

        // Set up Delete QR Code button functionality
        deleteQRCodeButton.setOnClickListener(view -> deleteQRCode(eventId));
    }


    /**
     * Loads the QR code details for the given event ID and displays it if available.
     */
    private void loadQRCode(String eventId) {
        firestore.collection("QRData")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // QR Code data exists
                        String qrContent = task.getResult().getDocuments().get(0).getString("qrContent");
                        if (qrContent != null) {
                            displayQRCode(qrContent);
                        }
                    } else {
                        Log.d("EventDetailsAdmin", "No QR Code found for this event.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Generates and displays a QR code in the ImageView from the provided content.
     *
     * @param qrContent The content to encode in the QR code.
     */
    private void displayQRCode(String qrContent) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);
            qrCodeImageView.setVisibility(View.VISIBLE); // Ensure it's visible
        } catch (WriterException e) {
            Log.e("EventDetailsAdmin", "Error generating QR Code", e);
            Toast.makeText(this, "Error displaying QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteQRCode(String eventId) {
        if (eventId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Query the QR code data collection for the matching event ID
            db.collection("QRData")
                    .whereEqualTo("eventId", eventId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Delete each document matching the event ID
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference()
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "QR Code deleted successfully.", Toast.LENGTH_SHORT).show();
                                            // Hide the QR Code ImageView
                                            qrCodeImageView.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(this, "No QR Code found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error querying QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Invalid event ID.", Toast.LENGTH_SHORT).show();
        }
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
                String posterUrl = snapshot.getString("posterUrl"); // Retrieve the poster URL


                getFacilityInfo(snapshot.getString("organizerId"));
                // Display event details on the UI
                eventNameTitleView.setText(eventName);
                eventDateView.setText("Date: " + date);
                eventLocationView.setText("Location: " + location);
                eventDetailsView.setText(eventDetails);
                if (posterUrl != null && !posterUrl.isEmpty()) {
                    Picasso.get()
                            .load(posterUrl)
                            .placeholder(R.drawable.poster_placeholder) // Default placeholder image
                            .error(R.drawable.poster_error) // Error placeholder
                            .into(eventPosterView);
                } else {
                    eventPosterView.setImageResource(R.drawable.poster_placeholder);
                }

            } else {
                Log.e("EventDetailsAdminActivity", "Event not found");
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
                    if (waitlistId != null) {
                        waitListRepository.deleteWaitList(waitlistId);
                        Log.d("EventDetailsAdminActivity", "waitlist id: " + waitlistId);
                    }
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
     * Gets information of the facility that organized the event.
     * @param organizerId to associate id with facility
     */
    private void getFacilityInfo(String organizerId) {
        firestore.collection("users").document(organizerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facilityName = documentSnapshot.getString("facilityName");
                        String contactInfo = documentSnapshot.getString("contactInfo");

                        // set facility info in textview
                        facilityNameTextView.setText("Facility: " + facilityName);
                        facilityContactInfoTextView.setText("Contact Info: \n" + contactInfo);
                    }
                });
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
