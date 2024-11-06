package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrganizerCreateEvent extends AppCompatActivity {
    private EditText eventNameEditText, eventLocationEditText, eventDetailsEditText, eventDateEditText;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private WaitListRepository waitListRepository;
    private UserImpl organizer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize EventRepository and UserRepository
        //needs testing
        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        waitListRepository = new WaitListRepository();
        db = FirebaseFirestore.getInstance();

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
            date = new SimpleDateFormat("dd-mm-yyyy").parse(dateString);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming the organizer is already logged in and available
        // get current organizer
        userRepository.getSingleUser(getIntent().getStringExtra("userId"), new UserRepository.FirestoreCallbackSingleUser() {
            public void onSuccess(UserImpl user) {
                Log.d(TAG, "Successfully got current user!");
                organizer = user;
            }
            public void onFailure(Exception e){
                Log.d(TAG, "Error getting current user");
            }
        });
        // create event with current user as organizer
        Event event = new Event(organizer);
        event.setEventName(eventName);
        event.setLocation(location);
        event.setEventDetails(details);
        event.setDate(date);
        event.setOrganizerId(getIntent().getStringExtra("userId"));
        // create waitlist for event
        /**
         * todo
         * let organizer fill out field for waitlist size limit
         */
        WaitList waitList = new WaitList(30,
                event.getEventName() + "Waitlist",
                event.getEventName(),
                new ArrayList<UserImpl>());
        // generate a waitlist id
        waitList.setWaitListId(db.collection("waitlist").document().getId());

        // Add waitlist and event to Firestore
        eventRepository.addEvent(event, waitList.getWaitListId());
        waitListRepository.createWaitList(waitList, waitList.getWaitListId(), event.getEventName());


        Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
    }
}
