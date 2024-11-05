package com.example.goldencarrot.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.db.EventRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrganizerCreateEvent extends AppCompatActivity {
    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize EventRepository
        //needs testing
        eventRepository = new EventRepository();

        // Set up UI components
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventLocationEditText = findViewById(R.id.eventLocationEditText);
        eventDetailsEditText = findViewById(R.id.eventDetailsEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        Button createEventButton = findViewById(R.id.createEventButton);

        // Set onClickListener for the Create Event button
        createEventButton.setOnClickListener(view -> createEvent());
    }

    private void createEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String location = eventLocationEditText.getText().toString().trim();
        String details = eventDetailsEditText.getText().toString().trim();
        String dateString = eventDateEditText.getText().toString().trim();

        if (eventName.isEmpty() || location.isEmpty() || details.isEmpty() || dateString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming the organizer is already logged in and available
        UserImpl organizer = new UserImpl(); // Replace with actual organizer ID
        Event event = new Event(organizer);
        event.setEventName(eventName);
        event.setLocation(location);
        event.setEventDetails(details);
        event.setDate(date);

        // Add event to Firestore
        //eventRepository.addEvent(event);

        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();

    }
}
