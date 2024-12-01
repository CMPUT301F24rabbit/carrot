package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This Activity allows the event organizer to create a new event and upload a poster for it.
 * It contains fields for event name, location, details, date, and poster image.
 * The event can optionally have geolocation and a waitlist limit.
 */
public class OrganizerCreateEvent extends AppCompatActivity {

    private static final String TAG = "OrganizerCreateEvent";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText, eventLimitEditText;
    private Switch geolocationSwitch;
    private ImageView eventPosterImageView;
    private Button createEventButton, selectPosterButton;

    private Uri posterUri;
    private boolean geolocationIsEnabled;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private String eventId;

    private FirebaseFirestore db;
    private UserImpl organizer;
    private boolean geolocationIsEnabled;
    private String organizerId, facilityName, location, email, phoneNumber, facilityDescription;

    /**
     * Called when the activity is created.
     * Initializes the UI components and sets up listeners for creating events and selecting posters.
     * If an event ID is provided, it will load the poster for the event.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        db = FirebaseFirestore.getInstance();
 

        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLimitEditText = findViewById(R.id.waitlistLimitEditText);
        geolocationSwitch = findViewById(R.id.geolocation);
        eventPosterImageView = findViewById(R.id.eventPosterImageView);
        createEventButton = findViewById(R.id.createEventButton);
        selectPosterButton = findViewById(R.id.selectPosterButton);
        geolocation = findViewById(R.id.geolocation);

        Button createEventButton = findViewById(R.id.createEventButton);
        Button backButton = findViewById(R.id.backButtonFromCreateEvent);

        // get facility details
        getFacilityLocation();

        // location default to facility location
        eventLocationEditText.setText(location);

        geolocation.toggle();
        geolocation.setText("Enable geolocation:");

        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> geolocationIsEnabled = isChecked);

        selectPosterButton.setOnClickListener(view -> selectPosterImage());
        createEventButton.setOnClickListener(view -> createEvent());

        // Get event ID from intent and load the poster if it exists
        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventPoster(eventId);
        }

        editEventPosterListener();
    }

    /**
     * Launches an intent to allow the user to select an image from their device for the event poster.
     */
    private void selectPosterImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection activity. If an image is selected,
     * it sets the image in the ImageView and uploads it to Firebase Storage.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData();
            Log.d(TAG, "Selected poster URI: " + posterUri); // Log the selected URI
            eventPosterImageView.setImageURI(posterUri);
            if (eventId != null) {
                uploadPosterToFirebase(eventId);
            }
        }
    }

    /**
     * Uploads the selected poster image to Firebase Storage.
     * Once uploaded, it fetches the download URL and updates Firestore with the poster URL.
     *
     * @param eventId The ID of the event for which the poster is being uploaded.
     */
    private void uploadPosterToFirebase(String eventId) {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("posters/" + eventId + "_poster.jpg");

        Log.d(TAG, "Uploading poster for eventId: " + eventId); // Log event ID being uploaded

        storageRef.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Poster uploaded successfully for eventId: " + eventId); // Confirm upload
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "Fetched poster URL: " + uri.toString()); // Log the fetched poster URL
                        // Update Firestore with the new poster URL
                        //documentReferend.update("posterUrl", uri.toString())
                                //.addOnSuccessListener(aVoid -> Log.d(TAG, "Poster URL updated in Firestore"))
                                //.addOnFailureListener(e -> Log.e(TAG, "Failed to update poster URL in Firestore", e));
                    }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch poster URL", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to upload poster for eventId: " + eventId, e));

        });
        organizerId = getDeviceId(OrganizerCreateEvent.this);

        // Set onClickListener for the Create Event button
        createEventButton.setOnClickListener(view -> {
            createEvent();
            Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHomeView.class);
            startActivity(intent);
        });
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHomeView.class);
            startActivity(intent);
        });
    }

    /**
     * Loads the event poster from Firebase Storage and displays it in the ImageView.
     * If the poster does not exist, a default image is shown.
     *
     * @param eventId The ID of the event whose poster is to be fetched.
     */
    private void loadEventPoster(String eventId) {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("posters/" + eventId + "_poster.jpg");

        Log.d(TAG, "Fetching poster URL for eventId: " + eventId); // Log event ID for which poster is being fetched

        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "Fetched poster URL: " + uri.toString()); // Log the fetched URL
                    Glide.with(this).load(uri).into(eventPosterImageView); // Load poster into ImageView
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to fetch poster URI for eventId: " + eventId, e); // Log error if fetching fails
                    // Optionally set a default image if fetching fails
                    eventPosterImageView.setImageResource(R.drawable.poster_placeholder);
                });
    }

    /**
     * Creates an event using the data provided by the organizer, including the event poster.
     * If successful, the event is created in the database, and a success message is shown.
     * If there is an error, a failure message is shown.
     */
    private void createEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String location = eventLocationEditText.getText().toString().trim();
        String details = eventDetailsEditText.getText().toString().trim();
        String dateString = eventDateEditText.getText().toString().trim();
        String limitString = eventLimitEditText.getText().toString().trim();

        if (eventName.isEmpty() || location.isEmpty() || details.isEmpty() || dateString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (posterUri == null) {
            Toast.makeText(this, "Please select a poster image", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = getDeviceId(OrganizerCreateEvent.this);
        Log.d(TAG, "Organizer ID: " + organizerId);

        // Get the organizer user details
 
        userRepository.getSingleUser(organizerId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                Event event = new Event(user);
                event.setEventName(eventName);
                event.setLocation(location);
                event.setEventDetails(details);
                event.setDate(date);
                event.setOrganizerId(organizerId);
                event.setGeolocationEnabled(geolocationIsEnabled);
                Integer waitlistLimit = null;
                if (!limitString.isEmpty()) {
                    try {
                        waitlistLimit = Integer.parseInt(limitString);
                        if (waitlistLimit < 0) {
                            Toast.makeText(OrganizerCreateEvent.this, "Waitlist limit must be a positive number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(OrganizerCreateEvent.this, "Waitlist limit must be a number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                eventRepository.addEvent(event, posterUri, waitlistLimit, new EventRepository.EventCallback() {
                    @Override
                    public void onSuccess(Event event) {
                        Toast.makeText(OrganizerCreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        Toast.makeText(OrganizerCreateEvent.this, "Successfully created event: " +
                                event.getEventName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(OrganizerCreateEvent.this, "Event creation failed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to create event", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrganizerCreateEvent.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to fetch user data", e);
            }
        });
    }

    private void editEventPosterListener() {
        eventPosterImageView.setOnLongClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            return true;
        });
    }

    /**
     * Retrieves the unique device ID for the current device.
     * This ID is used as the organizer's ID when creating an event.
     *
     * @param context The context of the current activity.
     * @return The unique device ID.
     */
    private String getDeviceId(Context context) {
        String organizerId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Fetched ANDROID_ID: " + organizerId);
        return organizerId;
    }
}


    private void getFacilityLocation() {
        db.collection("users").document(organizerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        location = documentSnapshot.getString("facilityLocation");
                    }
                });
    }
}
 
