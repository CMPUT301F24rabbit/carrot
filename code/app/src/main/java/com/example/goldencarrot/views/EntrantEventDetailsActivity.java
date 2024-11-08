package com.example.goldencarrot.views;

import android.app.AlertDialog;
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
import com.example.goldencarrot.utils.FirestoreCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * The {@code EntrantEventDetailsActivity} displays detailed information about a specific event
 * and allows the user to join a waitlist for the event. The activity interacts with Firestore to
 * retrieve event details, manage the waitlist, and handle geolocation-related logic.
 */
public class EntrantEventDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private TextView eventDetailsTextView;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private WaitListDb waitListRepository;
    private WaitList eventWaitList;
    private boolean isUserInWaitList;
    private boolean isGeolocationEnabled;

    /**
     * Initializes the activity, sets up the UI, and loads the event details and waitlist data.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_details_view);

        initializeRepositories();
        setupUI();
        loadEventDetails();
        loadWaitList();
    }

    /**
     * Initializes the repositories required for managing event details and waitlist data.
     */
    private void initializeRepositories() {
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        userRepository = new UserRepository();
        waitListRepository = new WaitListRepository();
    }

    /**
     * Sets up the UI components, including the back button and the join waitlist button.
     */
    private void setupUI() {
        // Initialize TextView for displaying event details
        eventDetailsTextView = findViewById(R.id.entrant_eventDetailsTextView);

        // Set up back button to finish the activity
        Button backButton = findViewById(R.id.entrant_backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up join waitlist button
        Button joinWaitListButton = findViewById(R.id.entrant_join_waitlist_button);
        joinWaitListButton.setOnClickListener(view -> handleJoinWaitList());
    }

    /**
     * Loads the event details based on the event ID passed in the intent.
     *
     * @throws IllegalArgumentException If no event ID is provided in the intent.
     */
    private void loadEventDetails() {
        String eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            eventRepository.getBasicEventById(eventId, new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    isGeolocationEnabled = event.getGeolocationEnabled();
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

    /**
     * Displays the event details in the UI.
     *
     * @param event The event object containing the event details.
     */
    private void displayEventDetails(Event event) {
        eventDetailsTextView.setText(String.format(
                "Event Name: %s\nEvent Details: %s\nLocation: %s\nDate: %s",
                event.getEventName(),
                event.getEventDetails(),
                event.getLocation(),
                event.getDate()
        ));
    }

    /**
     * Loads the waitlist data for the event based on the event ID passed in the intent.
     */
    private void loadWaitList() {
        String eventId = getIntent().getStringExtra("eventId");
        waitListRepository.getWaitListByEventId(eventId, new FirestoreCallback<WaitList>() {
            @Override
            public void onSuccess(WaitList result) {
                eventWaitList = result;
                User user = new UserImpl();
                user.setUserId(getDeviceId(EntrantEventDetailsActivity.this));
                checkAndJoinWaitList(user);
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Error fetching waitlist details");
            }
        });
    }

    /**
     * Handles the action of joining the event waitlist. If geolocation is enabled, a confirmation
     * dialog is shown before adding the user to the waitlist.
     *
     * @throws IllegalArgumentException If the event ID or user ID is null.
     */
    private void handleJoinWaitList() {
        String eventId = getIntent().getStringExtra("eventId");
        String uid = getDeviceId(this);
        User user = new UserImpl();
        user.setUserId(uid);

        if (isGeolocationEnabled) {
            // Show the dialog if geolocation is enabled
            showGeolocationDialog(user, eventId);
        } else {
            // Proceed directly if geolocation is not enabled
            fetchWaitListAndJoin(user, eventId);
        }
    }

    /**
     * Displays a dialog to confirm the user's intent to join the waitlist when geolocation is enabled.
     *
     * @param user The user attempting to join the waitlist.
     * @param eventId The event ID for the event the user is trying to join.
     */
    private void showGeolocationDialog(User user, String eventId) {
        new AlertDialog.Builder(this)
                .setTitle("Geolocation is enabled for this event")
                .setMessage("Are you sure you want to join?")
                .setPositiveButton("Yes", (dialog, which) -> fetchWaitListAndJoin(user, eventId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Fetches the waitlist data for the event and attempts to add the user to the waitlist.
     *
     * @param user The user attempting to join the waitlist.
     * @param eventId The event ID for the event the user is trying to join.
     */
    private void fetchWaitListAndJoin(User user, String eventId) {
        waitListRepository.getWaitListByEventId(eventId, new FirestoreCallback<WaitList>() {
            @Override
            public void onSuccess(WaitList result) {
                eventWaitList = result;
                checkAndJoinWaitList(user);
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Something went wrong :(");
            }
        });
    }

    /**
     * Checks if the user is already in the waitlist for the event and attempts to add them if not.
     *
     * @param user The user attempting to join the waitlist.
     */
    private void checkAndJoinWaitList(User user) {
        waitListRepository.isUserInWaitList(eventWaitList.getWaitListId(), user, new FirestoreCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isUserInWaitList = result;
                if(isUserInWaitList) {
                    showToast("User  already in waitlist");
                } else {
                    addUserToWaitList(user);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.w("WaitListCheck", "Error checking if user is in waitlist", e);
            }
        });
    }

    /**
     * Adds the user to the waitlist for the event.
     *
     * @param user The user to be added to the waitlist.
     */
    private void addUserToWaitList(User user) {
        waitListRepository.addUserToWaitList(eventWaitList.getWaitListId(), user, new FirestoreCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                showToast("Added to waitlist");
                Intent intent = new Intent(EntrantEventDetailsActivity.this, EntrantHomeView.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Error adding user to waitlist");
            }
        });
    }

    /**
     * Displays a short toast message.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(EntrantEventDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Retrieves the unique device ID for the current device.
     *
     * @param context The context of the application.
     * @return The unique device ID.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Cleans up any ongoing listener registrations when the activity is stopped.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
