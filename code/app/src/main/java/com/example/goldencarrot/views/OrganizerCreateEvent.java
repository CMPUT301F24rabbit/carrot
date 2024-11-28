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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OrganizerCreateEvent extends AppCompatActivity {

    private static final String TAG = "OrganizerCreateEvent";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText, eventLimitEditText;
    private Switch geolocationSwitch;
    private ImageView eventPosterImageView;

    private Uri posterUri;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private UserImpl organizer;
    private boolean geolocationIsEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize repositories
        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        // Set up UI components
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLimitEditText = findViewById(R.id.waitlistLimitEditText);
        geolocationSwitch = findViewById(R.id.geolocation);
        eventPosterImageView = findViewById(R.id.eventPosterImageView);

        Button createEventButton = findViewById(R.id.createEventButton);
        Button selectPosterButton = findViewById(R.id.selectPosterButton);

        geolocationSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            geolocationIsEnabled = isChecked;
            geolocationSwitch.setText(isChecked ? "Enable geolocation:" : "Disable geolocation:");
        });

        selectPosterButton.setOnClickListener(view -> selectPosterImage());

        // Authenticate user
        String deviceId = getDeviceId(this);
        userRepository.getSingleUser(deviceId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                if ("organizer".equals(user.getUserType())) {
                    organizer = user;
                    createEventButton.setOnClickListener(view -> createEvent());
                } else {
                    Toast.makeText(OrganizerCreateEvent.this, "Unauthorized access. Redirecting...", Toast.LENGTH_SHORT).show();
                    redirectToOrganizerHome();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to authenticate user.", e);
                Toast.makeText(OrganizerCreateEvent.this, "Authentication failed. Redirecting...", Toast.LENGTH_SHORT).show();
                redirectToOrganizerHome();
            }
        });
    }

    private void redirectToOrganizerHome() {
        Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHomeView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

        Integer waitlistLimit = null;
        if (!limitString.isEmpty()) {
            try {
                waitlistLimit = Integer.parseInt(limitString);
                if (waitlistLimit < 0) {
                    Toast.makeText(this, "Waitlist limit must be a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Waitlist limit must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Event event = new Event(organizer);
        event.setEventName(eventName);
        event.setLocation(location);
        event.setEventDetails(details);
        event.setDate(date);
        event.setOrganizerId(organizer.getUserId());
        event.setGeolocationEnabled(geolocationIsEnabled);

        Integer finalWaitlistLimit = waitlistLimit;
        uploadPosterToFirebase(posterUri, posterUrl -> {
            event.setPosterUrl(posterUrl);
            eventRepository.addEvent(event, String.valueOf(finalWaitlistLimit), new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    Toast.makeText(OrganizerCreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(OrganizerCreateEvent.this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void uploadPosterToFirebase(Uri imageUri, OnSuccessListener<String> onSuccessListener) {
        String eventId = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("/posters/" + eventId + "_poster.jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    onSuccessListener.onSuccess(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload poster", e);
                    Toast.makeText(this, "Failed to upload poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
