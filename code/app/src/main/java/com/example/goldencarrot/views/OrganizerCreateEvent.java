package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrganizerCreateEvent extends AppCompatActivity {
    private static final String TAG = "OrganizerCreateEvent";
    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText, eventLimitEditText;
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
        Button createEventButton = findViewById(R.id.createEventButton);

        // Set onClickListener for the Create Event button
        createEventButton.setOnClickListener(view -> {
            createEvent();
            Intent intent = new Intent(OrganizerCreateEvent.this, OrganizerHomeView.class);
            startActivity(intent);
        });
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

        // Get the organizer user details
        String organizerId = getIntent().getStringExtra("userId");
        userRepository.getSingleUser(organizerId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                Log.d(TAG, "Successfully got current user!");
                organizer = user;

                // Create event with the organizer
                Event event = new Event(organizer);
                event.setEventName(eventName);
                event.setLocation(location);
                event.setEventDetails(details);
                event.setDate(date);
                event.setOrganizerId(organizerId);

                // Parse waitlist limit if provided; if empty, it defaults to no limit
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

                // Add the event to the repository with the optional waitlist limit
                eventRepository.addEvent(event, waitlistLimit);

                // Create a waitlist if needed
                WaitList waitList = new WaitList();
                waitList.setLimitNumber(waitlistLimit); // Set limit from user input, or null if no limit
                waitList.setEventId(event.getEventId());
                waitList.setUserArrayList(new ArrayList<>());
                waitListRepository.createWaitList(waitList, waitList.getWaitListId(), event.getEventName());

                Toast.makeText(OrganizerCreateEvent.this, "Event created successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error getting current user");
                Toast.makeText(OrganizerCreateEvent.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
