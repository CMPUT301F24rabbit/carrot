package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListDb;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.example.goldencarrot.controller.RanBackground;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;



import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * The {@code EntrantEventDetailsActivity} displays detailed information about a specific event
 * and allows the user to join a waitlist for the event. The activity interacts with Firestore to
 * retrieve event details, manage the waitlist, and handle geolocation-related logic.
 */
public class EntrantEventDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private ImageView eventPosterImageView;
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

        // Apply RNG Background
        RelativeLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));

        initializeRepositories();
        setupUI();
        // Extract eventId from the intent or QR code URL
        String eventId = getEventIdFromIntent(getIntent());

        if (eventId != null) {
            loadEventDetails(eventId);  // Load event details using the eventId
            loadWaitList(eventId);      // Load waitlist using the eventId
        } else {
            showToast("No event ID provided");
        }
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
        eventPosterImageView = findViewById(R.id.eventPosterView);

        // Set up back button to finish the activity
        Button backButton = findViewById(R.id.entrant_backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantEventDetailsActivity.this,
                    EntrantHomeView.class);
            startActivity(intent);
        });

        // Set up join waitlist button
        Button joinWaitListButton = findViewById(R.id.entrant_join_waitlist_button);
        joinWaitListButton.setOnClickListener(view -> handleJoinWaitList());
    }

    /**
     * Extracts the eventId from the intent's data (QR code URL).
     *
     * @param intent The intent that started the activity.
     * @return The eventId if it exists in the URL, otherwise null.
     */
    private String getEventIdFromIntent(Intent intent) {
        String eventId = intent.getStringExtra("eventId");

        // Check if the eventId is provided in the intent as a QR code URL
        Uri uri = intent.getData();  // Get the URI from the intent's data
        if (uri != null && uri.getScheme().equals("goldencarrot")) {  // Ensure the scheme is correct
            eventId = uri.getQueryParameter("eventId");  // Extract the eventId from the URL query
        }

        return eventId;
    }
    /**
     * Loads the event details based on the event ID passed in the intent.
     *
     * @throws IllegalArgumentException If no event ID is provided in the intent.
     */
    private void loadEventDetails(String eventId) {
        if (eventId != null) {
            eventRepository.getBasicEventById(eventId, new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    isGeolocationEnabled = event.getGeolocationEnabled();
                    displayEventDetails(eventId);
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
     * @param eventId The eventId containing the event details.
     */
    private void displayEventDetails(String eventId) {
        DocumentReference eventRef = firestore.collection("events").document(eventId);
        listenerRegistration = eventRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("EntrantEventDetailsActivity", "Error fetching event details", e);
                Toast.makeText(this, "Error fetching event details", Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                // Retrieve event details from Firestore
                String eventName = snapshot.getString("eventName");
                String eventDetails = snapshot.getString("eventDetails");
                String location = snapshot.getString("location");
                String date = snapshot.getString("date");
                String posterUrl = snapshot.getString("posterUrl");

                Log.d(TAG, "Fetched event details: " +
                        "\nEvent Name: " + eventName +
                        "\nEvent Details: " + eventDetails +
                        "\nLocation: " + location +
                        "\nDate: " + date +
                        "\nPoster URL: " + posterUrl);


                // get facility profile details
                firestore.collection("users").document(snapshot.getString("organizerId"))
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Log.d(TAG, "Fetched organizer details");
                                String facilityName = documentSnapshot.getString("facilityName");
                                String contactInfo = documentSnapshot.getString("contactInfo");

                                // Display event details on the UI
                                eventDetailsTextView.setText(String.format(
                                        "%s\nFacility: %s\nEvent Details: %s\nLocation: %s\nDate: %s\nContact Info:\n%s",
                                        eventName,
                                        facilityName,
                                        eventDetails,
                                        location,
                                        date,
                                        contactInfo
                                ));
                            } else{
                                Log.e(TAG, "No organizer details found in Firestore");
                            }
                        })
                        .addOnFailureListener(e1 -> Log.e(TAG, "Erorror fetching organizer details", e1));

                if(posterUrl != null && !posterUrl.isEmpty()) {
                    Log.d(TAG, "Loading postere image from URL: " + posterUrl);
                    Picasso.get()
                            .load(posterUrl)
                            .placeholder(R.drawable.poster_placeholder)
                            .error(R.drawable.profilepic1)
                            .into(eventPosterImageView, new com.squareup.picasso.Callback(){
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Poster image loaded successfully");
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "Error loading poster image", e);
                                }
                            });
                } else {
                    Log.w(TAG, "Poster URL is null or empty, skipping image loading");
                }

                // Optionally, load an image for the event poster if available
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads the waitlist data for the event based on the event ID passed in the intent.
     */
    private void loadWaitList(String eventId) {
        if (eventId != null) {
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
        } else {
            showToast("No event ID provided");
        }
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
        waitListRepository.getWaitListByEventId(eventId, new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                eventWaitList = waitList;
                if (waitList.isFull()) {
                    showToast("Sorry, the event's waitlist is already full. Check back later!");
                } else {
                    checkAndJoinWaitList(user);
                }
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
        // Fetch the event details again to ensure we have the latest organizer and geolocation data
        eventRepository.getBasicEventById(eventWaitList.getEventId(), new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                // If geolocation is enabled, validate entrant's location
                if (event.getGeolocationEnabled()) {
                    validateEntrantLocation(user, event.getOrganizerId());
                } else {
                    proceedToJoinWaitList(user);
                }
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Error fetching event details");
            }
        });
    }

    private void validateEntrantLocation(User user, String organizerId) {
        userRepository.getSingleUser(organizerId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl organizer) {
                String facilityCity = getCityFromCoordinates(organizer.getLatitude(), organizer.getLongitude());
                Log.d(TAG, "Organizer's facility city: " + facilityCity);

                userRepository.getSingleUser(user.getUserId(), new UserRepository.FirestoreCallbackSingleUser() {
                    @Override
                    public void onSuccess(UserImpl entrant) {
                        if (entrant.getLatitude() == null || entrant.getLongitude() == null) {
                            showToast("Entrant location not available. Cannot join waitlist.");
                            Log.e(TAG, "Entrant's latitude or longitude is null.");
                            return;
                        }

                        String entrantCity = getCityFromCoordinates(entrant.getLatitude(), entrant.getLongitude());
                        Log.d(TAG, "Entrant's city: " + entrantCity);

                        if (entrantCity != null && entrantCity.equalsIgnoreCase(facilityCity)) {
                            Log.d(TAG, "Entrant is in the same city as the facility.");
                            proceedToJoinWaitList(user);
                        } else {
                            showToast("Cannot join the waitlist since you are not located in the same city.");
                            Log.w(TAG, "Entrant is in a different city. Restriction applied.");
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        showToast("Error fetching entrant data");
                        Log.e(TAG, "Error fetching entrant document: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                showToast("Error fetching organizer data");
                Log.e(TAG, "Error fetching organizer document: " + e.getMessage());
            }
        });
    }



    private void proceedToJoinWaitList(User user) {
        waitListRepository.isUserInWaitList(eventWaitList.getWaitListId(), user, new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                isUserInWaitList = (boolean) result;
                if (isUserInWaitList) {
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
    }

    // Helper function to get city from coordinates
    private String getCityFromCoordinates(Double latitude, Double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                Log.d(TAG, "Fetched city from coordinates: " + city);
                return city;
            } else {
                Log.w(TAG, "No addresses found for coordinates: " + latitude + ", " + longitude);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder error: " + e.getMessage());
        }
        return null;
    }



    /**
     * Adds the user to the waitlist for the event.
     *
     * @param user The user to be added to the waitlist.
     */
    private void addUserToWaitList(User user) {
        waitListRepository.addUserToWaitList(eventWaitList.getWaitListId(), user, new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                showToast("Added to waitlist");

                // Navigate back to home view after adding to waitlist
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
