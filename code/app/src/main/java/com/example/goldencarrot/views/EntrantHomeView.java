package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.CircleTransform;
import com.example.goldencarrot.controller.NotificationController;
import com.example.goldencarrot.controller.RanBackground;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.event.EventArrayAdapter;
import com.example.goldencarrot.controller.RanBackground;
import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationPermissionRequester;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class handles all the features and interactions for the Entrant's home screen.
 * It displays the user's profile, lists of upcoming and waitlisted events, and allows the user
 * to explore events, go to the waitlist, and view notifications.
 * The data is loaded from Firestore and displayed in the appropriate UI elements.
 */
public class EntrantHomeView extends AppCompatActivity {
    // UI elements
    /**
     * Diplays information useful to the Entrant
     */
    private TextView usernameTextView;
    private TextView waitlistedEventsTitle;
    private ImageView profileImageView;
    private ListView upcomingEventsListView;
    private ListView waitlistedEventsListView;
    private Button exploreEventsButton;
    private Button notificationsButton;

    // Firestore references and data
    private FirebaseFirestore firestore;
    private EventArrayAdapter upcomingEventsAdapter;
    private EventArrayAdapter waitlistedEventsAdapter;
    private ArrayList<Event> upcomingEventsList;
    private ArrayList<Event> waitlistedEventsList;
    private NotificationRepository notificationRepository;
    private NotificationController notifController;
    private ArrayList<Notification> notifications;
    private ActivityResultLauncher<String> resultLauncher;
    private NotificationPermissionRequester notificationPermissionRequester;
    private boolean notificationPermission;
    /**
     * Called when the activity is first created. Initializes the UI components,
     * loads user data, and sets up event listeners.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_home_view);

        // Apply RNG Background
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));

        // Initialize the views from layout file
        profileImageView = findViewById(R.id.entrant_home_view_image_view);
        usernameTextView = findViewById(R.id.entrant_home_view_user_name);
        upcomingEventsListView = findViewById(R.id.upcoming_events);
        waitlistedEventsListView = findViewById(R.id.waitlisted_events);
        exploreEventsButton = findViewById(R.id.button_explore_events);
        notificationsButton = findViewById(R.id.notifications_button);
        waitlistedEventsTitle = findViewById(R.id.waitlisted_events_title);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission Granted
                        notificationPermission = true;
                        Log.i(TAG, "permission granted for notifications");
                    } else {
                        // permission Denied
                        notificationPermission = false;
                        Toast.makeText(EntrantHomeView.this, "You currently have app notifications off", Toast.LENGTH_LONG).show();
                        Toast.makeText(EntrantHomeView.this, "Turn on notifications to receive messages " +
                                "about your waitlist status", Toast.LENGTH_LONG).show();


                    }
                });

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");

        // This method is decoupled for testing purposes
        if (notificationPermissionRequester == null) {
            notificationPermissionRequester = new DefaultPermissionRequester();
        }
        notificationPermissionRequester.requestNotificationPermission();

        // Event lists and adapter Initialization
        upcomingEventsList = new ArrayList<>();
        waitlistedEventsList = new ArrayList<>();
        upcomingEventsAdapter = new EventArrayAdapter(this, upcomingEventsList);
        waitlistedEventsAdapter = new EventArrayAdapter(this, waitlistedEventsList);
        notifications = new ArrayList<>();

        // Set adapters to listview
        upcomingEventsListView.setAdapter(upcomingEventsAdapter);
        waitlistedEventsListView.setAdapter(waitlistedEventsAdapter);

        // Set user name
        loadUserData();

        // Open WaitlistActivity
        setListenersForWaitlistActivity(waitlistedEventsListView, waitlistedEventsTitle, WaitlistActivity.class);

        // Set the click listener for the "Explore Events" button (add event functionality)
        exploreEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantHomeView.this, BrowseEventsActivity.class);
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

        // Notifications Button
        notificationsButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantHomeView.this, EntrantNotificationsActivity.class);
            startActivity(intent);
        });

        // QR scanner button
        Button scanQrButton = findViewById(R.id.entrant_scan_qr_button);
        scanQrButton.setOnClickListener(view -> startQrScanner());

        // Load event data
        loadEventData();

        //display all notifications
        notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());
        notifController = new NotificationController();

        notificationRepository.getNotificationsByUserId(getDeviceId(this),
                new NotificationRepository.NotificationCallback<List<Notification>>() {
                    @Override
                    public void onSuccess(List<Notification> result) {
                        Log.d(TAG, "Got notifications: " + notifications.toString());
                        notifications.clear();
                        notifications.addAll(result);
                        notifController.displayNotifications(notifications, EntrantHomeView.this, notificationPermission);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EntrantHomeView.this, "Error fetching notifications",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Starts a QR Scanner activity for scanning event-related QR codes.
     */
    private void startQrScanner() {
        new IntentIntegrator(this).initiateScan();  // This will launch the QR scanner
    }

    /**
     * Processes the result of the QR scanner activity
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
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
     * Loads user data from firestore and updates the UI.
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
                        String userProfileImage = documentSnapshot.getString("profileImage");

                        // Phone number to optional string
                        Optional<String> optionalPhoneNumber = (phoneNumber != null && !phoneNumber.isEmpty())
                                ? Optional.of(phoneNumber)
                                : Optional.empty();

                        UserImpl user = null;
                        try {
                            user = new UserImpl(email, userType, name, optionalPhoneNumber, notificationAdministrator, notificationOrganizer, userProfileImage);
                            if (user.getName() != null) {
                                usernameTextView.setText(user.getName());
                                Log.d(TAG, "Username loaded: " + user.getName());
                            } else {
                                Log.w(TAG, "Username field is missing in the document");
                                usernameTextView.setText("Error: Username not found");
                            }

                            // Profile Image set
                            if(userProfileImage != null && !userProfileImage.isEmpty()){
                                loadProfileImage(userProfileImage);
                            } else {
                                Log.w(TAG, "Profile image URL is missing, leaving blank.");
                                profileImageView.setImageDrawable(null);
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
     * Loads event data from Firestore, including the waitlisted and upcoming events for the user.
     */
    private void loadEventData() {
        String deviceId = getDeviceId(EntrantHomeView.this);
        Log.d("EntrantHomeView", "DeviceID: " + deviceId);

        // Load the first 4 waitlisted events from Firestore
        Set<String> processedEventIds = new HashSet<>(); // Track them because for some reason
        // THEY ARE ADDING DOUBLE
        Set<String> upcomingEventIds = new HashSet<>(); // Track them because for some reason

        waitlistedEventsList.clear();

        firestore.collection("waitlist").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot waitlistDoc : task.getResult()) {
                    processWaitlistDocument(waitlistDoc, deviceId, processedEventIds);

                }

                for (QueryDocumentSnapshot upcomingDoc : task.getResult()) {
                    processUpcomingDocument(upcomingDoc, deviceId, upcomingEventIds);
                }

            } else {
                Log.e("EntrantHomeView", "Error loading waitlisted events", task.getException());
            }
        });
    }

    /**
     * Process a Firestore document to get the waitlisted data for the current user
     * @param waitlistDoc The firestore document representing a waitlisted event.
     * @param deviceId The device ID of the current user.
     * @param processedEventIds A set to track processed event IDs and prevent duplicates
     */
    private void processWaitlistDocument(QueryDocumentSnapshot waitlistDoc, String deviceId, Set<String> processedEventIds) {
        // Check if device id is in the user map with waiting status
        Map<String, Object> usersMap = (Map<String, Object>) waitlistDoc.get("users");

        if (usersMap != null && usersMap.containsKey(deviceId)) {
            String status = (String) usersMap.get(deviceId);

            if (UserUtils.WAITING_STATUS.equals(status)) {
                // Get EVENT ID!!!!!!!!!!!!!!!!!!!!!!!!!!
                String eventId = waitlistDoc.getString("eventId");

                if (eventId != null && processedEventIds.add(eventId)) {
                    String docType = "waitlist";
                    fetchEventDetails(eventId, docType);
                } else {
                Log.d("loadEventData", "User is in a waitlist for evenId: " + eventId);
                }
            }
        }
    }

    /**
     * Process a Firestore document to extract upcoming event data for a user
     * @param upcomingDoc The firestore document representing an upcoming event
     * @param deviceId The device Id of the current user.
     * @param processedEventIds A set to track processed event IDs and prevent duplicates
     */
    private void processUpcomingDocument(QueryDocumentSnapshot upcomingDoc, String deviceId, Set<String> processedEventIds) {
        // Check if device id is in the user map with waiting status
        Map<String, Object> usersMap = (Map<String, Object>) upcomingDoc.get("users");

        if (usersMap != null && usersMap.containsKey(deviceId)) {
            String status = (String) usersMap.get(deviceId);

            if (UserUtils.ACCEPTED_STATUS.equals(status) || UserUtils.CHOSEN_STATUS.equals(status)) {
                // Get EVENT ID!!!!!!!!!!!!!!!!!!!!!!!!!!
                String eventId = upcomingDoc.getString("eventId");

                if (eventId != null && processedEventIds.add(eventId)) {
                    String docType = "upcoming";
                    fetchEventDetails(eventId, docType);
                } else {
                    Log.d("loadEventData", "User is in a upcoming for evenId: " + eventId);
                }
            }
        }
    }

    /**
     * Fetches detailed information about an event from Firestore
     * @param eventId The ID of the event we are fetching
     * @param docType The type of document: "waitlist" or "upcoming".
     */
    private void fetchEventDetails(String eventId, String docType) {
        // GET DEM EVENT DETAILS!!!!!
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    if (eventDoc.exists()) {
                        Event event = getEventDetailsFromDoc(eventDoc);
                        if (event != null && !isEventDuplicate(event, docType) && docType.equals("waitlist")) {
                            // Only add if not already in the list
                            waitlistedEventsList.add(event);
                            waitlistedEventsAdapter.notifyDataSetChanged();
                            Log.d("fetchEventDetails", "Event added to waitlist: " + event.getEventName());
                        } else if(event != null && !isEventDuplicate(event, docType) && docType.equals("upcoming")){
                            upcomingEventsList.add(event);
                            upcomingEventsAdapter.notifyDataSetChanged();
                            Log.d("fetchEventDetials", "Event added to upcoming: " + event.getEventName());
                        } else {
                            Log.d("fetchEventDetials", "Duplicate event ignored: " + event.getEventName());
                        }
                    } else {
                        Log.e("fetchEventDetaisl", "Event document does not exist for event Id: " + eventId);
                    }
                })
                .addOnFailureListener(e -> Log.e("fetchEventDetaisl", "Error fetching event detils", e));
    }

    /**
     * Determines if an event is already in the list
     * @param event The event we are checking to see if it is a duplicate
     * @param docType The type  of document: "waitlist" or "upcoming"
     * @return True if the event is a duplicate, false otherwise.
     */
    private boolean isEventDuplicate(Event event, String docType) {
        // Check if event already exists in the list
        if (docType.equals("waitlist")) {
            for (Event existingEvent : waitlistedEventsList) {
                if (existingEvent.getEventName().equals(event.getEventName())) {
                    return true;
                }
            }
        } else if (docType.equals("upcoming")) {
            for (Event eventExists : upcomingEventsList) {
                if (eventExists.getEventName().equals(event.getEventName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extracts event details from a Firestore document and createes an Event object
     * @param eventDoc The Firestore document containing the event data.
     * @return An Event object, or null if the data could not be extracted
     */
    private Event getEventDetailsFromDoc(DocumentSnapshot eventDoc) {
        try {
            String eventName = eventDoc.getString("eventName");
            String location = eventDoc.getString("location");
            String details = eventDoc.getString("details");
            String posterUrl = eventDoc.getString("posterUrl");


            Date eventDate = datesMakeMeCry(eventDoc.get("date"));

            Event event = new Event(new UserImpl());
            event.setEventName(eventName);
            event.setLocation(location);
            event.setEventDetails(details);
            event.setDate(eventDate);
            event.setPosterUrl(posterUrl);

            Log.d("getEventDetailsFromDoc", "Got details of event: " + eventName);
            return event;
        } catch (Exception e) {
            Log.e("getEventDetialsFromDoc", "Failed to get details of efent: ", e);
            return null;
        }
    }

    /**
     * Converts a raw date into a Date object
     * @param rawDate The data from firestore
     * @return A Date object, or null if it didn't work.
     */
    private Date datesMakeMeCry(Object rawDate) {

        if (rawDate instanceof com.google.firebase.Timestamp) {
            return ((com.google.firebase.Timestamp) rawDate).toDate();
        } else if (rawDate instanceof String) {
            try {
                return new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse((String) rawDate);
            } catch (Exception e) {
                Log.e("loadEventData", "Failed to parse date string", e);
            }
        }
        return null;
    }

    /**
     * Requests permission from user to enable notifications
     */
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // permission already granted
                Log.d("EntrantHomeView", "permission already granted for notifications");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // toast explaining to user that app needs permission to send notifications to them
                Toast.makeText(EntrantHomeView.this, "Golden Carrot needs permission to send notifications.",
                        Toast.LENGTH_SHORT).show();
            } else {
                //request permission
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
        }
    }

    /**
     * Todo deprecate this private method
     */
    public void setPermissionRequester(NotificationPermissionRequester permissionRequester) {
        this.notificationPermissionRequester = permissionRequester;
    }

    // Default implementation for production
    private class DefaultPermissionRequester implements NotificationPermissionRequester {
        @Override
        public void requestNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        EntrantHomeView.this, Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "permission not granted for notifications");
                    // Request the permission
                    ActivityCompat.shouldShowRequestPermissionRationale(EntrantHomeView.this, Manifest.permission.POST_NOTIFICATIONS);
                    ActivityCompat.requestPermissions(
                            EntrantHomeView.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            1);
                    resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                        EntrantHomeView.this, Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(EntrantHomeView.this, "Golden Carrot needs permission to send notifications.",
                            Toast.LENGTH_LONG).show();
                } else {
                    resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }

            }
        }
    }

    /**
     * Load the profile image for the entrant from an URL.
     * @param imageUrl TheURLofthe profile image.
     */
    private void loadProfileImage(String imageUrl){
        if(!isGenericImage(imageUrl)) {
            Picasso.get().load(imageUrl)
                    .transform(new CircleTransform())
                    .error(R.drawable.profilepic1)
                    .into(profileImageView);
        } else {
            Picasso.get().load(imageUrl)
                    .error(R.drawable.profilepic1)
                    .into(profileImageView);
        }
    }

    /**
     * Checks whether the current profile image is determanistically generated
     * @param imageUrl The URL of the profile image
     * @return True if the image is generic, false otherwise
     */
    private boolean isGenericImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return false;

        // Use Uri.decode() for decoding URL to handle encoded characters like `%2F`
        String decodedUrl = Uri.decode(imageUrl);
        Log.d(TAG, "Decoded URL: " + decodedUrl);

        // Check if the decoded URL contains the "profile/generic/" path
        return decodedUrl.contains("/profile/generic/");
    }
}
