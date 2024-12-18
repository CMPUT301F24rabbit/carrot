package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.MainActivity;
import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.controller.RanBackground;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * The {@code BrowseEventsActivity} class is responsible for displaying a list of events
 * that users (either admins or participants) can browse in the app.
 * This activity interacts with Firebase Firestore to load and display events from the "events" collection.
 * It also manages user-specific navigation and event selection behavior based on the user's type.
 */
public class BrowseEventsActivity extends AppCompatActivity {
    private static final String TAG = "BrowseEventsActivity";

    private FirebaseFirestore firestore;
    private CollectionReference eventsCollection;
    private ListView eventsListView;
    private ArrayAdapter<String> eventsAdapter;
    private ArrayList<String> eventsList;
    private ArrayList<DocumentSnapshot> eventDocuments;

    private Button backButton;
    private String deviceId;
    private String currentUserType;
    private UserRepository userRepository;

    /**
     * This method is called when the activity is created. It sets up the UI, initializes the necessary components,
     * and fetches events from Firestore.
     * It also retrieves the user type and handles item click actions for navigating to event details.
     *
     * @param savedInstanceState a Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Apply RNG Background
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));

        // Initialize Firestore, Collections, and user repo
        firestore = FirebaseFirestore.getInstance();
        eventsCollection = firestore.collection("events");
        userRepository = new UserRepository();

        // Initialize ListView and Adapter
        eventsListView = findViewById(R.id.eventsListView);
        eventsList = new ArrayList<>();
        eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsList);
        eventsListView.setAdapter(eventsAdapter);

        // Initialize list for event documents
        eventDocuments = new ArrayList<>();

        // Fetch events from Firestore
        loadEventsFromFirestore();

        // Get user type of current device
        deviceId = getDeviceId(this);
        userRepository.checkUserExistsAndGetUserType(deviceId, new UserRepository.UserTypeCallback() {
            @Override
            public void onResult(boolean exists, String userType) {
                if (exists) {
                    // User exists, and we have the userType
                    Log.d("BrowseEventsActivity", "User Type: " + userType);
                    currentUserType = userType;
                } else {
                    // Failed to get user type
                    Log.d("BrowseEventsActivity", "Error: failed to get UserType");
                }
            }
        });

        // Set an item click listener to open EventDetailsActivity
        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected event document
            DocumentSnapshot selectedDocument = eventDocuments.get(position);
            String documentId = selectedDocument.getId();

            // Start EventDetailsAdminActivity and pass document ID as an extra
            if (currentUserType.equals("ADMIN")) {
                Intent intent = new Intent(BrowseEventsActivity.this, EventDetailsAdminActivity.class);
                intent.putExtra("eventId", documentId);
                startActivity(intent);
            } else if (currentUserType.equals("PARTICIPANT")) {
                Intent intent = new Intent(BrowseEventsActivity.this, EntrantEventDetailsActivity.class);
                intent.putExtra("eventId", documentId);
                startActivity(intent);
            }
        });

        // Handle back button click to navigate back based on user type
        backButton = findViewById(R.id.browseEventsBackBtn);
        backButton.setOnClickListener(view -> {
            if (currentUserType.equals("ADMIN")) {
                Intent intent = new Intent(BrowseEventsActivity.this, AdminHomeActivity.class);
                startActivity(intent);
            } else if (currentUserType.equals("PARTICIPANT")) {
                Intent intent = new Intent(BrowseEventsActivity.this, EntrantHomeView.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Loads events from Firestore and updates the ListView.
     * Fetches documents from the "events" collection and adds event names to the list.
     * Also stores the document snapshots for later use when the event is selected.
     */
    private void loadEventsFromFirestore() {
        eventsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventsList.clear();
                        eventDocuments.clear(); // Clear previous documents in case of refresh
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Assuming each document has a "name" field for event name
                                String eventName = document.getString("eventName");
                                eventsList.add(eventName);
                                eventDocuments.add(document); // Store the document snapshot for later access
                            }
                            eventsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Returns the unique device ID for the current device.
     *
     * @param context the application context.
     * @return the unique device ID.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
