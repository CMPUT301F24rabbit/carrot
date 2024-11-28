package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
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
    private Switch geolocation;
    private ImageView eventPosterImageView;
    private Button createEventButton, selectPosterButton;

    private Uri posterUri;
    private boolean geolocationIsEnabled;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private WaitListRepository waitListRepository;
    private UserImpl organizer;

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
        eventPosterImageView = findViewById(R.id.eventPosterImageView);
        createEventButton = findViewById(R.id.createEventButton);
        selectPosterButton = findViewById(R.id.selectPosterButton);

        // Geolocation toggle setup
        geolocation.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            geolocationIsEnabled = isChecked;
            geolocation.setText(isChecked ? "Disable geolocation:" : "Enable geolocation:");
        });

        // Set up select poster button
        selectPosterButton.setOnClickListener(view -> selectPosterImage());

        // Set up create event button
        createEventButton.setOnClickListener(view -> {
            createEvent();
            Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHomeView.class);
            startActivity(intent);
        });
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

        String organizerId = getDeviceId(this);
        userRepository.getSingleUser(organizerId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                Log.d(TAG, "Successfully got current user!");
                organizer = user;

                Event event = new Event(organizer);
                event.setEventName(eventName);
                event.setLocation(location);
                event.setEventDetails(details);
                event.setDate(date);
                event.setOrganizerId(organizer.getUserId());
                event.setGeolocationEnabled(geolocationIsEnabled);

                // Additional logic for uploading the poster and handling the event can go here
                uploadPosterToFirebase(posterUri, url -> {
                    event.setPosterUrl(url);
                    eventRepository.addEvent(event, Uri.parse(url).toString(), EventRepository.EventCallback);
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error getting current user", e);
                Toast.makeText(OrganizerCreateEvent.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Integer parseWaitlistLimit(String limitString) {
        if (limitString.isEmpty()) return null;
        try {
            int limit = Integer.parseInt(limitString);
            if (limit < 0) {
                Toast.makeText(this, "Waitlist limit must be positive.", Toast.LENGTH_SHORT).show();
                return null;
            }
            return limit;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Waitlist limit must be a number.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadPosterToFirebase(Uri imageUri, OnSuccessListener<String> onSuccessListener) {
        String eventId = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("/posters/" + eventId + "_poster.jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> onSuccessListener.onSuccess(uri.toString())))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload poster", e);
                    Toast.makeText(this, "Failed to upload poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

