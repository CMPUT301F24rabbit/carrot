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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.NotificationController;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.event.EventArrayAdapter;

import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationPermissionRequester;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    /**
     * Called when the activity is first created. Initializes the UI components,
     * loads user data, and sets up event listeners.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_home_view);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission Granted
                        Toast.makeText(EntrantHomeView.this, "You will now receive notifications!", Toast.LENGTH_LONG).show();
                    } else {
                        // permission Denied
                        Toast.makeText(EntrantHomeView.this, "You will not receive notifications", Toast.LENGTH_LONG).show();
                        ;
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

        // Set user name
        loadUserData();

        // Initialize the views from layout file
        profileImageView = findViewById(R.id.entrant_home_view_image_view);
        usernameTextView = findViewById(R.id.entrant_home_view_user_name);
        upcomingEventsListView = findViewById(R.id.upcoming_events);
        waitlistedEventsListView = findViewById(R.id.waitlisted_events);
        exploreEventsButton = findViewById(R.id.button_explore_events);
        notificationsButton = findViewById(R.id.notifications_button);
        waitlistedEventsTitle = findViewById(R.id.waitlisted_events_title);

        // Event lists and adapter Initialization
        upcomingEventsList = new ArrayList<>();
        waitlistedEventsList = new ArrayList<>();
        upcomingEventsAdapter = new EventArrayAdapter(this, upcomingEventsList);
        waitlistedEventsAdapter = new EventArrayAdapter(this, waitlistedEventsList);
        notifications = new ArrayList<>();

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
                        notifController.displayNotifications(notifications, EntrantHomeView.this);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EntrantHomeView.this, "Error fetching notifications",
                                Toast.LENGTH_SHORT).show();
                    }
                });

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
                        String userProfileImage = documentSnapshot.getString("profileImage");

                        // Phone number to optional string
                        Optional<String> optionalPhoneNumber = (phoneNumber != null && !phoneNumber.isEmpty())
                                ? Optional.of(phoneNumber)
                                : Optional.empty();

                        try {
                            UserImpl user = new UserImpl(email, userType, name, optionalPhoneNumber, notificationAdministrator, notificationOrganizer, userProfileImage);
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
                                Log.w(TAG, "Profile  image URL is missing, leaving blank.");
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

                        //
                        if (UserUtils.WAITING_STATUS.equals(status) || UserUtils.ACCEPTED_STATUS.equals(status) || UserUtils.CHOSEN_STATUS.equals(status)) {
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
                    // Request the permission
                    ActivityCompat.requestPermissions(
                            EntrantHomeView.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            1);
                }
            }
        }
    }

    private void loadProfileImage(String imageUrl){
        Picasso.get().load(imageUrl)
                .into(profileImageView, new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Profile image loaded successfully.");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Failed to load profile image", e);
                    }
                });
    }
}
