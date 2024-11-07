package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListDb;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class EntrantEventDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private TextView eventDetailsTextView;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private WaitListDb waitListRepository;
    private WaitList eventWaitList;
    private boolean isUserInWaitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_details_view);

        initializeRepositories();
        setupUI();
        loadEventDetails();
        loadWaitList();
    }

    // Data Injection
    private void initializeRepositories() {
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        waitListRepository = new WaitListRepository();
    }

    private void setupUI() {
        // Initialize TextView
        eventDetailsTextView = findViewById(R.id.entrant_eventDetailsTextView);

        // Set up back button
        Button backButton = findViewById(R.id.entrant_backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up join waitlist button
        Button joinWaitListButton = findViewById(R.id.entrant_join_waitlist_button);
        joinWaitListButton.setOnClickListener(view -> handleJoinWaitList());
    }

    private void loadEventDetails() {
        String eventId = getIntent().getStringExtra("documentId");
        if (eventId != null) {
            eventRepository.getBasicEventById(eventId, new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    displayEventDetails(event);
                }

                @Override
                public void onFailure(Exception e) {
                    showToast("Error fetching event details");
                }
            });
        } else {
            showToast("No event ID provided");
        }
    }

    private void displayEventDetails(Event event) {
        eventDetailsTextView.setText(String.format(
                "Event Name: %s\nEvent Details: %s\nLocation: %s\nDate: %s",
                event.getEventName(),
                event.getEventDetails(),
                event.getLocation(),
                event.getDate()
        ));
    }

    private void loadWaitList() {
        String eventId = getIntent().getStringExtra("eventId");
        waitListRepository.getWaitListByEventId(eventId, new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                eventWaitList = waitList;
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Error fetching waitlist details");
            }
        });
    }

    private void handleJoinWaitList() {
        String eventId = getIntent().getStringExtra("eventId");
        String uid = getDeviceId(this);
        User user = new UserImpl();
        user.setUserId(uid);

        waitListRepository.getWaitListByEventId(eventId, new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                eventWaitList = waitList;
                checkAndJoinWaitList(user);
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Something went wrong :(");
            }
        });
    }

    private void checkAndJoinWaitList(User user) {
        waitListRepository.isUserInWaitList(eventWaitList.getWaitListId(), user, new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                isUserInWaitList = (boolean) result;
                if (isUserInWaitList) {
                    Log.d("WaitListCheck", "User is in the waitlist.");
                    showToast("User already in waitlist");
                } else {
                    addUserToWaitList(user);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("WaitListCheck", "Error checking if user is in waitlist", e);
            }
        });

        Intent intent = new Intent(EntrantEventDetailsActivity.this, EntrantHomeView.class);
        startActivity(intent);

    }

    private void addUserToWaitList(User user) {
        waitListRepository.addUserToWaitList(eventWaitList.getWaitListId(), user, new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                showToast("Added to waitlist");
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Error adding user to waitlist");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(EntrantEventDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
