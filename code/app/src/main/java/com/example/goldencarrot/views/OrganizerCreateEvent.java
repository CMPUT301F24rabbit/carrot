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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        eventRepository = new EventRepository();
        userRepository = new UserRepository();

        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventLimitEditText = findViewById(R.id.waitlistLimitEditText);
        geolocationSwitch = findViewById(R.id.geolocation);
        eventPosterImageView = findViewById(R.id.eventPosterImageView);
        createEventButton = findViewById(R.id.createEventButton);
        selectPosterButton = findViewById(R.id.selectPosterButton);

        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> geolocationIsEnabled = isChecked);

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

        String organizerId = getDeviceId(OrganizerCreateEvent.this);
        Log.d(TAG, "Organizer ID: " + organizerId);

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

    private String getDeviceId(Context context) {
        String organizerId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Fetched ANDROID_ID: " + organizerId);

        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
