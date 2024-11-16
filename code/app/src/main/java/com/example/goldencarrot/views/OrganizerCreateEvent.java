package com.example.goldencarrot.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Activity for the organizer to create a new event, including details such as event name,
 * location, description, date, and optional geolocation settings. The organizer can also set
 * a limit for the event's waitlist.
 * This activity handles the creation of an event, stores it in the database, and generates
 * a waitlist if a limit is set. Additionally, it allows for generating a QR code for the event.
 */
public class OrganizerCreateEvent extends AppCompatActivity {

    private static final String TAG = "OrganizerCreateEvent";
    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText, eventLimitEditText;
    private Switch geolocation;
    private ImageView qrCodeImageView;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private WaitListRepository waitListRepository;
    private boolean geolocationIsEnabled;
    private Event createdEvent;

    /**
     * Initializes the activity, sets up the UI components, and handles geolocation switch changes.
     * Sets up onClickListeners for the Create Event button and Generate QR Code button, which trigger
     * event creation and QR code generation, respectively.
     *
     * @param savedInstanceState The saved instance state of the activity, if any.
     */
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
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        Button createEventButton = findViewById(R.id.createEventButton);
        Button generateQRCodeButton = findViewById(R.id.generateQRCodeButton);

        geolocation.setOnCheckedChangeListener((buttonView, isChecked) -> geolocationIsEnabled = isChecked);

        // Set onClickListener for the Create Event button
        createEventButton.setOnClickListener(view -> createEvent());

        // Set onClickListener for the Generate QR Code button
        generateQRCodeButton.setOnClickListener(view -> {
            if (createdEvent == null) {
                Toast.makeText(this, "Please create an event first", Toast.LENGTH_SHORT).show();
                return;
            }
            generateQRCode();
        });
    }

    /**
     * Creates a new event based on the details entered by the organizer and adds it to the event repository.
     * Optionally creates a waitlist if a waitlist limit is provided.
     *
     * @throws ParseException If the date format is invalid.
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

        createdEvent = new Event();
        createdEvent.setEventName(eventName);
        createdEvent.setLocation(location);
        createdEvent.setEventDetails(details);
        createdEvent.setDate(date);
        createdEvent.setGeolocationEnabled(geolocationIsEnabled);

        Integer waitlistLimit = null;
        if (!limitString.isEmpty()) {
            try {
                waitlistLimit = Integer.parseInt(limitString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Waitlist limit must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        eventRepository.addEvent(createdEvent, waitlistLimit);
        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates a QR code for the created event, encoding the event's details such as name,
     * location, date, and description. Displays the QR code in an ImageView.
     */
    private void generateQRCode() {
        if (createdEvent == null || createdEvent.getEventId() == null) {
            Toast.makeText(this, "Please create an event first or ensure it has an ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encode the event ID as QR code content
        String qrContent = "goldencarrot://eventDetails?eventId=" + createdEvent.getEventId();

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e(TAG, "QR Code generation error", e);
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

}
