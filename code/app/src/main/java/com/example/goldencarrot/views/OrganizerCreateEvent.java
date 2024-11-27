package com.example.goldencarrot.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OrganizerCreateEvent extends AppCompatActivity {

    private static final String TAG = "OrganizerCreateEvent";
    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText, eventLimitEditText;
    private Switch geolocation;
    private ImageView eventPosterImageView;
    private Uri posterUri; // To store the selected image URI
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private WaitListRepository waitListRepository;
    private UserImpl organizer;
    private boolean geolocationIsEnabled;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize repositories
        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        waitListRepository = new WaitListRepository();

        // Set up UI components
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLimitEditText = findViewById(R.id.waitlistLimitEditText);
        geolocation = findViewById(R.id.geolocation);
        eventPosterImageView = findViewById(R.id.eventPosterImageView); // Add ImageView in your XML layout

        Button createEventButton = findViewById(R.id.createEventButton);
        Button selectPosterButton = findViewById(R.id.selectPosterButton); // Add this button in your XML layout

        geolocation.toggle();
        geolocation.setText("Enable geolocation:");
        geolocation.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            geolocationIsEnabled = isChecked;
            geolocation.setText(isChecked ? "Enable geolocation:" : "Disable geolocation:");
        });

        selectPosterButton.setOnClickListener(view -> selectPosterImage());
        createEventButton.setOnClickListener(view -> createEvent());
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
            eventPosterImageView.setImageURI(posterUri); // Display selected image in the ImageView
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

        String organizerId = getDeviceId(this);
        userRepository.getSingleUser(organizerId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                organizer = user;
                Event event = new Event(organizer);
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

                Integer finalWaitlistLimit = waitlistLimit;
                uploadPosterToFirebase(posterUri, posterUrl -> {
                    event.setPosterUrl(posterUrl); // Set the poster URL
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

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrganizerCreateEvent.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPosterToFirebase(Uri imageUri, OnSuccessListener<String> onSuccessListener) {
        String eventId = UUID.randomUUID().toString(); // Generate a unique ID for the event
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("events/posters/" + eventId + "_poster.jpg");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Convert Uri to String and call the provided onSuccessListener
                    String posterUrl = uri.toString();
                    onSuccessListener.onSuccess(posterUrl); // Now passing the String URL
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get poster URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

