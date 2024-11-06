package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.event.EventArrayAdapter;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * This is a class that handles all entrant app features.
 */
public class EntrantHomeView extends AppCompatActivity {

    // Initialize UI
    private TextView usernameTextView;
    private TextView waitlistedEventsTitle;
    private ImageView profileImageView;
    private ListView upcomingEventsListView;
    private ListView waitlistedEventsListView;
    private Button exploreEventsButton;
    private Button goToWaitlistButton;

   // Firestore
    private FirebaseFirestore firestore;
    private EventArrayAdapter upcomingEventsAdapter;
    private EventArrayAdapter waitlistedEventsAdapter;
    private ArrayList<Event> upcomingEventsList;
    private ArrayList<Event> waitlistedEventsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_home_view);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // Initialize the views from layout file
        profileImageView = findViewById(R.id.entrant_home_view_image_view);
        usernameTextView = findViewById(R.id.entrant_home_view_user_name);
        upcomingEventsListView = findViewById(R.id.upcoming_events);
        waitlistedEventsListView = findViewById(R.id.waitlisted_events);
        exploreEventsButton = findViewById(R.id.button_explore_events);
        waitlistedEventsTitle = findViewById(R.id.waitlisted_events_title);

        // Set user name
        loadUserData();

        // Event lists and adapter Inititalization
        upcomingEventsList =new ArrayList<>();
        waitlistedEventsList =  new ArrayList<>();
        upcomingEventsAdapter = new EventArrayAdapter(this, upcomingEventsList);
        waitlistedEventsAdapter = new EventArrayAdapter(this, waitlistedEventsList);

        // Set adapters to listview
        upcomingEventsListView.setAdapter(upcomingEventsAdapter);
        waitlistedEventsListView.setAdapter(waitlistedEventsAdapter);

        // Open WaitlistActivity
        setListenersForWaitlistActivity(waitlistedEventsListView, waitlistedEventsTitle, WaitlistActivity.class);

        // Do the same thing for upcoming events when we get that far

        // Set on long click for profile editing
        profileImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Go to profile change view
                Intent intent = new Intent(EntrantHomeView.this, EntrantEditUserDetailsView.class);
                startActivity(intent);
                return true;
            }
        });

        // Set the click listener for the "Explore Events" button (add event functionality)
        exploreEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Events Exploration Activity
                Intent intent = new Intent(EntrantHomeView.this, BrowseEventsActivity.class);
                startActivity(intent);
            }
        });

        // Load event data
        loadEventData();
    }

    private void setListenersForWaitlistActivity(ListView listView, TextView titleView, Class<?> activityClass){
        // Open when title is clicked
        titleView.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantHomeView.this, activityClass);
            startActivity(intent);
        });

        // Open when List view is long clicked
        listView.setOnItemLongClickListener((parent, view, position, id) ->{
            Intent intent = new Intent(EntrantHomeView.this, activityClass);
            startActivity(intent);
            return true;
        });
    }

    // Load user Data
    private void loadUserData(){
        String deviceId = getDeviceId(EntrantHomeView.this);

        firestore.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Manually construct UserImpl with data from Firestore to handle Optional fields
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String userType = documentSnapshot.getString("userType");
                        String phoneNumber = documentSnapshot.getString("phoneNumber"); // Firestore stores as String

                        // Phone number to optional string
                        Optional<String> optionalPhoneNumber = (phoneNumber != null && !phoneNumber.isEmpty())
                                ? Optional.of(phoneNumber)
                                : Optional.empty();
                        try {
                            UserImpl user = new UserImpl(email, userType, name, optionalPhoneNumber);
                            if (user.getName() != null) {
                                usernameTextView.setText(user.getName());
                                Log.d(TAG, "Username loaded: " + user.getName());
                            } else {
                                Log.w(TAG, "Username field is missing in the document");
                                usernameTextView.setText("Error: Username not found");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating UserImpl object: " + e.getMessage(), e);
                            usernameTextView.setText("Error loading user data");
                        }
                    } else {
                        Log.e(TAG, "Document does not exist");
                        usernameTextView.setText("Error: User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    usernameTextView.setText("Error fetching user data");

                });

    }

    /**
     * Retrieves the Android device ID.
     *
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Load Event Data
    private void loadEventData() {
        // Load the first 4 waitlisted events from firestore
        CollectionReference waitlistRef = firestore.collection("waitlist");
        waitlistRef.limit(4).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                waitlistedEventsList.clear(); // clear the current data
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get Event Data
                        String eventName = document.getString("eventName");
                        String location = document.getString("location");
                        String details = document.getString("details");
                        Date eventDate = document.getDate("date");
                        UserImpl organizer = document.toObject(UserImpl.class);

                        Log.d("EntrantHomeView", "Processing event: " + document.getId());

                        if(eventName != null){
                            Log.d("EntrantHOmeview", "Adding event:" + eventName);
                            Event event = new Event(organizer);
                            event.setEventName(eventName);
                            event.setLocation(location);
                            event.setEventDetails(details);
                            event.setDate(eventDate);

                            waitlistedEventsList.add(event);
                        }

                    }
                    // Notify adapter if changes
                    waitlistedEventsAdapter.notifyDataSetChanged();
            }
            else {
                // error stuff
                Log.e("EntrantHomeView",  "Error loading waitlisted events", task.getException());
            }

        });
    }
}
