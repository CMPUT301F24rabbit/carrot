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

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrganizerCreateEvent extends AppCompatActivity {
    private static final String TAG = "OrganizerCreateEvent";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText,
            eventDateEditText, eventLimitEditText;
    private Switch geolocationSwitch;
    private ImageView eventPosterImageView;
    private Button createEventButton, selectPosterButton, backButton;

    private Uri posterUri;
    private boolean geolocationIsEnabled;
    private String organizerId, location;
    private FirebaseFirestore db;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLimitEditText = findViewById(R.id.waitlistLimitEditText);
        geolocationSwitch = findViewById(R.id.geolocation);
        eventPosterImageView = findViewById(R.id.eventPosterImageView);
        createEventButton = findViewById(R.id.createEventButton);
        selectPosterButton = findViewById(R.id.selectPosterButton);
        backButton = findViewById(R.id.backButtonFromCreateEvent);

        // Load facility location and setup listeners
        getFacilityLocation();
        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> geolocationIsEnabled = isChecked);
        selectPosterButton.setOnClickListener(view -> selectPosterImage());
        createEventButton.setOnClickListener(view -> createEvent());
        backButton.setOnClickListener(view -> goOrganizerHomeView());
    }

    private void getFacilityLocation() {
        organizerId = getDeviceId(this);
        db.collection("users").document(organizerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        location = documentSnapshot.getString("facilityLocation");
                        eventLocationEditText.setText(location);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch facility location", e));
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void selectPosterImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            posterUri = data.getData();
            eventPosterImageView.setImageURI(posterUri);
        }
    }

    private void createEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String location = eventLocationEditText.getText().toString().trim();
        String details = eventDetailsEditText.getText().toString().trim();
        String dateString = eventDateEditText.getText().toString().trim();
        String limitString = eventLimitEditText.getText().toString().trim();

        if (eventName.isEmpty() || location.isEmpty() || details.isEmpty() || dateString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields and select a poster image.", Toast.LENGTH_SHORT).show();
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Use dd-MM-yyyy.", Toast.LENGTH_SHORT).show();
            return;
        }

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

                Integer waitlistLimit = limitString.isEmpty() ? null : Integer.parseInt(limitString);

                if (posterUri == null){
                    // Poster is null, create event with no poster
                    saveEventToFirestore(event, waitlistLimit);
                } else {
                    // Add Poster to FireStore and write event into events docRef
                    uploadPosterAndCreateEvent(event, waitlistLimit);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrganizerCreateEvent.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPosterAndCreateEvent(Event event, Integer waitlistLimit) {
        String posterPath = "posters/" + event.getEventId() + "_poster.jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(posterPath);

        storageRef.putFile(posterUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    event.setPosterUrl(uri.toString());
                    saveEventToFirestore(event, waitlistLimit);
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Poster upload failed.", Toast.LENGTH_SHORT).show());
    }

    private void saveEventToFirestore(Event event, Integer waitlistLimit) {
        eventRepository.addEvent(event, posterUri, waitlistLimit, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                Toast.makeText(OrganizerCreateEvent.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                goOrganizerHomeView();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrganizerCreateEvent.this, "Event creation failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goOrganizerHomeView(){
        Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHomeView.class);
        startActivity(intent);
    }
}
