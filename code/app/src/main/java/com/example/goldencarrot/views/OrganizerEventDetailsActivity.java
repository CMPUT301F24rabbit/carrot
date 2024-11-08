package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Activity that displays details of an event organized by the user.
 * It fetches event information from Firestore and displays it on the UI.
 * The organizer can also view the waitlisted, accepted, and declined entrants.
 */
public class OrganizerEventDetailsActivity extends AppCompatActivity {

    // Firestore and Event Repository initialization
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private EventRepository eventRepository;
    private String deviceID;
    private String eventId;
    private WaitListRepository waitListRepository;

    // UI Components
    private ImageView eventPosterView;
    private TextView eventNameTextView;
    private TextView eventDateTextView;
    private TextView eventLocationTextView;
    private TextView eventTimeTextView;
    private TextView eventDetailsTextView;
    private PopupWindow entrantsPopup;
    private Button selectLotteryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Firestore initialization
        firestore = FirebaseFirestore.getInstance();
        eventRepository = new EventRepository();
        waitListRepository = new WaitListRepository();
        List<User> usersWithStatus = new ArrayList<>();

        // Get eventID from Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId); // Load event details based on eventId
        } else {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
        }

        // UI Initialization
        eventPosterView = findViewById(R.id.event_DetailPosterView);
        eventNameTextView = findViewById(R.id.event_DetailNameTitleView);
        eventDateTextView = findViewById(R.id.event_DetailDateView);
        eventLocationTextView = findViewById(R.id.event_DetailLocationView);
        eventTimeTextView = findViewById(R.id.event_DetailTimeView);
        eventDetailsTextView = findViewById(R.id.event_DetailDetailsView);

        deviceID = getDeviceId(this);

        // Set up back button
        Button backButton = findViewById(R.id.back_DetailButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerHomeView.class);
            startActivity(intent);
        });

        // Hide delete button for organizer
        Button deleteEventBtn = findViewById(R.id.delete_DetailEventBtn);
        deleteEventBtn.setVisibility(View.INVISIBLE);

        // Entrants button: opens a popup showing Entrant options
        Button entrantsButton = findViewById(R.id.button_DetailViewEventLists);
        entrantsButton.setOnClickListener(v -> showEntrantsPopup());

        // Select Lottery Button: triggers the lottery dialog
        selectLotteryButton = findViewById(R.id.button_SelectLotteryUsers);
        // TODO: Implement lottery selection dialog where the organizer can choose
        // the number of users to approve for the event
        // selectLotteryButton.setOnClickListener(v -> showLotteryDialog());
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        listenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String organizerId = snapshot.getString("organizerId");

                if (organizerId != null && organizerId.equals(deviceID)) {
                    String eventName = snapshot.getString("eventName");
                    String eventDetails = snapshot.getString("eventDetails");
                    String location = snapshot.getString("location");
                    String date = snapshot.getString("date");
                    String time = snapshot.getString("time");
                    int posterResId = snapshot.getLong("posterResId") != null ? snapshot.getLong("posterResId").intValue() : R.drawable.default_poster;

                    eventNameTextView.setText(eventName);
                    eventDateTextView.setText("Date: " + date);
                    eventLocationTextView.setText("Location: " + location);
                    eventTimeTextView.setText("Time: " + time);
                    eventDetailsTextView.setText(eventDetails);
                    eventPosterView.setImageResource(posterResId);

                } else {
                    Toast.makeText(this, "Access denied: You are not authorized to view this event", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEntrantsPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_event_lists, null);

        entrantsPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        entrantsPopup.showAtLocation(findViewById(R.id.button_DetailViewEventLists), Gravity.CENTER, 0, 0);

        Button waitlistedButton = popupView.findViewById(R.id.button_EventDetailWaitlistedEntrants);
        Button acceptedButton = popupView.findViewById(R.id.button_EventDetailAcceptedEntrants);
        Button declinedButton = popupView.findViewById(R.id.button_EventDetailRejectedEntrants);

        waitlistedButton.setOnClickListener(v -> openEntrantsView("waiting"));
        acceptedButton.setOnClickListener(v -> openEntrantsView("accepted"));
        declinedButton.setOnClickListener(v -> openEntrantsView("declined"));
    }

    private void openEntrantsView(String status) {
        Intent intent = new Intent(OrganizerEventDetailsActivity.this, OrganizerWaitlistView.class);
        intent.putExtra("entrantStatus", status);
        intent.putExtra("eventId", eventId);
        entrantsPopup.dismiss();
        startActivity(intent);
    }
}
/*
    //dialog that allows organizer to select number of users to select
    private void showLotteryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter number of users to approve");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String inputText = input.getText().toString().trim();

            //input validation
            if (inputText.isEmpty()) {
                Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }
            int numToSelect;
            try {
                numToSelect = Integer.parseInt(inputText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call getUsersWithStatus to get users with "waiting" status
            waitListRepository.getUsersWithStatus(eventId, "waiting", usersWithStatus -> {
                if (usersWithStatus == null) {
                    Toast.makeText(this, "No users found with 'waiting' status.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Random random = new Random();
                Set<String> selectedUsers = new HashSet<>();

                //randomly generate
                while (selectedUsers.size() < numToSelect && selectedUsers.size() < usersWithStatus.size()) {
                    int randomIndex = random.nextInt(usersWithStatus.size());
                    selectedUsers.add(usersWithStatus.get(randomIndex));
                }

                for (String userId : selectedUsers) {
                    UserImpl user = new UserImpl(userId);
                    waitListRepository.updateUserStatusInWaitList(eventId, user, "approved");
                    //sendNotification(userId, "You have been selected for the event.");
                }

                Toast.makeText(this, numToSelect + " users have been selected.", Toast.LENGTH_SHORT).show();
            });

            // Setup alert dialog for confirmation
            new AlertDialog.Builder(this)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                    .show();
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (listenerRegistration != null) {
                listenerRegistration.remove();
            }
        }
    }
*/