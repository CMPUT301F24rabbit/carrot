package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.event.EventArrayAdapter;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
/**
 * This class handles all the features and interactions for the Entrant's home screen.
 * It displays the user's profile, lists of upcoming and waitlisted events, and allows the user
 * to explore events, go to the waitlist, and view notifications.
 * The data is loaded from Firestore and displayed in the appropriate UI elements.
 */
public class EntrantHomeView extends AppCompatActivity {

    // UI elements
    private TextView usernameTextView;
    private TextView waitlistedEventsTitle;
    private ImageView profileImageView;
    private ListView upcomingEventsListView;
    private ListView waitlistedEventsListView;
    private Button exploreEventsButton;
    private Button goToWaitlistButton;
    private Button notificationsButton;

    // Firestore references and data
    private FirebaseFirestore firestore;
    private EventArrayAdapter upcomingEventsAdapter;
    private EventArrayAdapter waitlistedEventsAdapter;
    private ArrayList<Event> upcomingEventsList;
    private ArrayList<Event> waitlistedEventsList;

    /**
     * Called when the activity is first created. Initializes the UI components,
     * loads user data, and sets up event listeners.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_home_view);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // Set user name
        loadUserData();

        // Initialize the views from layout file
        profileImageView = findViewById(R.id.entrant_home_view_image_view);
        usernameTextView = findViewById(R.id.entrant_home_view_user_name);
        upcomingEventsListView = findViewById(R.id.upcoming_events);
        waitlistedEventsListView = findViewById(R.id.waitlisted_events);
        exploreEventsButton = findViewById(R.id.button_explore_events);
        waitlistedEventsTitle = findViewById(R.id.waitlisted_events_title);
        notificationsButton = findViewById(R.id.notifications_button);

        // Event lists and adapter Initialization
        upcomingEventsList = new ArrayList<>();
        waitlistedEventsList = new ArrayList<>();
        upcomingEventsAdapter = new EventArrayAdapter(this, upcomingEventsList);
        waitlistedEventsAdapter = new EventArrayAdapter(this, waitlistedEventsList);

        // Set adapters to listview
        upcomingEventsListView.setAdapter(upcomingEventsAdapter);
        waitlistedEventsListView.setAdapter(waitlistedEventsAdapter);

        // Open WaitlistActivity
        setListenersForWaitlistActivity(waitlistedEventsListView, waitlistedEventsTitle, WaitlistActivity.class);

        // Set the click listener for the "Explore Events" button (add event functionality)
        exploreEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantHomeView.this, BrowseEventsActivity.class);
            startActivity(intent);
        });

        // Set the click listener for the "Notifications" button
        notificationsButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantHomeView.this, EntrantNotificationsActivity.class);
            startActivity(intent);
        });

        profileImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(EntrantHomeView.this, EntrantEditUserDetailsView.class);
                startActivity(intent);
                return true;
            }
        });

        // QR scanner button
        Button scanQrButton = findViewById(R.id.entrant_scan_qr_button);
        scanQrButton.setOnClickListener(view -> startQrScanner());

        // Load event data
        loadEventData();
    }
    private void startQrScanner() {
        new IntentIntegrator(this).initiateScan();  // This will launch the QR scanner
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Process the result from the QR scanner
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scannedContent = result.getContents();
            if (scannedContent != null && scannedContent.startsWith("goldencarrot://eventDetails")) {
                // If the QR code is valid and starts with the expected prefix, extract event ID
                Intent intent = new Intent(this, EntrantEventDetailsActivity.class);
                intent.setData(Uri.parse(scannedContent));
                startActivity(intent);
            } else {
                // Handle invalid QR code (optional)
                Toast.makeText(this, "Invalid QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }



    /**
     * Sets up listeners to open the WaitlistActivity when a waitlisted event is clicked.
     * @param listView The list view containing waitlisted events.
     * @param titleView The title view for waitlisted events.
     * @param activityClass The activity to open when a waitlist item is clicked.
     */
    private void setListenersForWaitlistActivity(ListView listView, TextView titleView, Class<?> activityClass) {
        // Open when title is clicked
        titleView.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantHomeView.this, activityClass);
            startActivity(intent);
        });

        // Open when ListView is long-clicked
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(EntrantHomeView.this, activityClass);
            startActivity(intent);
            return true;
        });
    }

    /**
     * Loads user data from Firestore and updates the UI.
     */
    private void loadUserData() {
        String deviceId = getDeviceId(EntrantHomeView.this);
        loadEventData();
        firestore.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Manually construct UserImpl with data from Firestore to handle Optional fields
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String userType = documentSnapshot.getString("userType");
                        String phoneNumber = documentSnapshot.getString("phoneNumber"); // Firestore stores as String
                        Boolean notificationAdministrator = documentSnapshot.getBoolean("administratorNotification");
                        Boolean notificationOrganizer = documentSnapshot.getBoolean("organizerNotification");

                        // Phone number to optional string
                        Optional<String> optionalPhoneNumber = (phoneNumber != null && !phoneNumber.isEmpty())
                                ? Optional.of(phoneNumber)
                                : Optional.empty();
                        try {
                            UserImpl user = new UserImpl(email, userType, name, optionalPhoneNumber, notificationAdministrator, notificationOrganizer);
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
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Loads waitlisted events data from Firestore and updates the UI.
     */
    private void loadEventData() {
        String deviceId = getDeviceId(EntrantHomeView.this);
        Log.d("EntrantHomeView", "DeviceID: " + deviceId);

        // Load the first 4 waitlisted events from Firestore
        CollectionReference waitlistRef = firestore.collection("waitlist");

        waitlistRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                waitlistedEventsList.clear(); // Clears the existing list

                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Check if device id is in the user map with waiting status
                    Map<String, Object> usersMap = (Map<String, Object>) document.get("users");

                    if (usersMap != null && usersMap.containsKey(deviceId)) {
                        String status = (String) usersMap.get(deviceId);

                        if (UserUtils.WAITING_STATUS.equals(status)) {
                            // Get event details and add it to the list
                            String eventName = document.getString("eventName");
                            String location = document.getString("location");
                            String details = document.getString("details");
                            Date eventDate = document.getDate("date");

                            if (eventName != null) {
                                Log.d("EntrantHomeView", "Adding event: " + eventName);

                                // Create event object and add it to the list
                                Event event = new Event(new UserImpl());
                                event.setEventName(eventName);
                                event.setLocation(location);
                                event.setEventDetails(details);
                                event.setDate(eventDate);

                                waitlistedEventsList.add(event);
                            }
                        }
                    }
                }

                Log.d("EntrantHomeView", "Total waitlisted events for user: " + waitlistedEventsList.size());

                // Notify data has changed
                waitlistedEventsAdapter.notifyDataSetChanged();
            } else {
                Log.e("EntrantHomeView", "Error loading waitlisted events", task.getException());
            }
        });
    }
}
