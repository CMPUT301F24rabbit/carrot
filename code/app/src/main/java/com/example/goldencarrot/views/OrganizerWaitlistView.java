package com.example.goldencarrot.views;
import static com.example.goldencarrot.data.model.user.UserUtils.ACCEPTED_STATUS;
import static com.example.goldencarrot.data.model.user.UserUtils.CANCELLED_STATUS;
import static com.example.goldencarrot.data.model.user.UserUtils.CHOSEN_STATUS;
import static com.example.goldencarrot.data.model.user.UserUtils.WAITING_STATUS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.RanBackground;
import com.example.goldencarrot.controller.NotificationController;
import com.example.goldencarrot.controller.WaitListController;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This class represents the activity where an organizer can view the waitlist for an event.
 * It fetches users from the waitlist based on their status (e.g., waiting or accepted)
 * and allows sending notifications to users based on their status in the waitlist.
 */
public class OrganizerWaitlistView extends AppCompatActivity {
    private ArrayList<String> userIdList;
    private ListView waitlistedUserListView;
    private TextView listTitle;
    private UserArrayAdapter userArrayAdapter;
    private FirebaseFirestore db;
    private WaitListRepository waitListRepository;
    private UserRepository userRepository;
    private ArrayList<User> waitlistedUserList;
    private String waitlistId;
    private NotificationRepository notifRepo;
    private NotificationController notifController;
    private Notification notification;
    private WaitListController waitListController;
    private boolean userGetsNotif;

    /**
     * Called when the activity is created. Initializes the Firestore instance, repositories,
     * and UI elements. Sets up the back button and notification button functionality.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlisted_users_list);

        // Apply RNG Background
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));


        userIdList = new ArrayList<>();
        waitlistedUserList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        waitListRepository = new WaitListRepository();
        userRepository = new UserRepository();
        notifRepo = new NotificationRepository(db);

        notifController = new NotificationController();

        // setting up layout views
        listTitle = findViewById(R.id.entrantsListTitle);
        waitlistedUserListView = findViewById(R.id.waitlistedUsersList);
        Button backBtn = findViewById(R.id.backButtonFromWaitlist);
        Button sendNotification = findViewById(R.id.sendNotificationButton);

        // if on the accepted list, we will hide the notification button
        if (getIntent().getStringExtra("entrantStatus").equals(ACCEPTED_STATUS)) {
            sendNotification.setVisibility(View.GONE);
        }

        Button cancelChosenEntrantsBtn = findViewById(R.id.cancel_chosen_entrants_button);

        // Fetch the waitlist data and initialize the waitlist controller
        waitListRepository.getWaitListByEventId(getIntent().getStringExtra("eventId"), new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                waitlistId = waitList.getWaitListId();
                fetchWaitlistedUsers(waitlistId);
                waitListController = new WaitListController(waitList);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerWaitlistView", "Failed to get waitlist");
            }
        });

        // Set up the adapter for the waitlisted users
        userArrayAdapter = new UserArrayAdapter(this, waitlistedUserList);
        waitlistedUserListView.setAdapter(userArrayAdapter);

        // Set the title for the list based on the entrant status
        listTitle.setText(getIntent().getStringExtra("entrantStatus").toUpperCase());

        // Set up back button click listener
        backBtn.setOnClickListener(view -> sendUserToPrevActivity());

        // Set up send notification button click listener
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotificationToAll();
            }
        });

        Button mapViewButton = findViewById(R.id.mapViewButton);
        mapViewButton.setOnClickListener(v -> {
            // Create intent to launch MapViewActivity
            Intent intent = new Intent(OrganizerWaitlistView.this, MapViewActivity.class);
            intent.putExtra("waitlistId", waitlistId); // Pass waitlistId to fetch locations
            startActivity(intent);
        });


        waitlistedUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (!getIntent().getStringExtra("entrantStatus").equals(ACCEPTED_STATUS)) {
                    sendNotificationToSingleUser(userIdList.get(position));
                }
            }
        });

        // Cancel Entrants Button is only visible if organizer is in the "chosen" waitlist
        if (!getIntent().getStringExtra("entrantStatus").equals(CHOSEN_STATUS)) {
            cancelChosenEntrantsBtn.setVisibility(View.GONE);
        }
        cancelChosenEntrantsBtn.setOnClickListener(view -> cancelAllChosenEntrants());
    }

    /**
     * Fetches the list of users from the waitlist based on their status (waiting or accepted).
     *
     * @param waitlistId The ID of the waitlist to fetch users from.
     */
    private void fetchWaitlistedUsers(String waitlistId) {
        waitListRepository.getUsersWithStatus(waitlistId, getIntent().getStringExtra("entrantStatus"), new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                userIdList = (ArrayList<String>) result;
                for (String userId : userIdList) {
                    Log.d("OrganizerWaitlistView", "adding user to " + getIntent().getStringExtra("entrantStatus") + ": " + userId);
                    userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                        @Override
                        public void onSuccess(UserImpl user) {
                            waitlistedUserList.add(user);
                            userArrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d("OrganizerWaitlistView", "Failed to get user from Firestore");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerWaitlistView", "Failed to get user list");
            }
        });
    }

    /**
     * Sends a notification to all users in the waitlist, based on their status (waiting/not chosen,
     * chosen, or cancelled).
     */
    private void sendNotificationToAll() {
        if (!userIdList.isEmpty()) {
            for (String userId : userIdList) {
                if (getNotificationPermission(userId)) {
                    createAndAddNotification(userId, getIntent().getStringExtra("entrantStatus"));
                }
            }
        } else {
            Toast.makeText(OrganizerWaitlistView.this, "list is empty, cannot send notification", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a notification for a user based on their status (waiting/not chosen, chosen, or cancelled).
     *
     * @param userId The ID of the chosen user.
     */
    private void createAndAddNotification(String userId, String status) {
        Notification notification;
        if (status.equals(WAITING_STATUS)) {
            notification = notifController.getOrCreateNotChosenNotification(
                    userId,
                    getIntent().getStringExtra("eventId"),
                    waitlistId);
        } else if (status.equals(CHOSEN_STATUS)) {
            notification = notifController.getOrCreateChosenNotification(
                    userId,
                    getIntent().getStringExtra("eventId"),
                    waitlistId
            );
        } else {
            notification = notifController.getOrCreateCancelledNotification(
                    userId,
                    getIntent().getStringExtra("eventId"),
                    waitlistId
            );
        }
        notifRepo.addNotification(notification, new NotificationRepository.NotificationCallback<Notification>() {
            @Override
            public void onSuccess(Notification result) {
                Toast.makeText(OrganizerWaitlistView.this, "added notification", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrganizerWaitlistView.this, "Notification not added", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Sends notification to select entrant
     * @param userId of user to receive notification
     */
    private void sendNotificationToSingleUser(String userId) {
        if (getNotificationPermission(userId)) {
            createAndAddNotification(userId, getIntent().getStringExtra("entrantStatus"));;
        }
    }

    /**
     * Gets permission of the user to receive notifications
     * @param userId
     * @return canReceive, true if they have notifications on, false otherwise
     */
    private boolean getNotificationPermission(String userId) {
        userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                userGetsNotif = user.getOrganizerNotifications();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerWaitlistView", "failed to get user");
            }
        });
        return userGetsNotif;
    }

    // Changes the status of "chosen" entrants to "cancelled"
    private void cancelAllChosenEntrants(){
        // change status in the model
        waitListController.updateChosenToCancelled();

        // update waitlist DB with the new model
        waitListRepository.updateWaitListInDatabase(waitListController.getWaitList());

        Toast.makeText(OrganizerWaitlistView.this, "Cancelled chosen " +
                "entrants", Toast.LENGTH_SHORT).show();

        // Send user to
        sendUserToPrevActivity();
    }

    // Sends user to OrganizerEventDetailsActivity
    private void sendUserToPrevActivity(){
        Intent intent = new Intent(OrganizerWaitlistView.this, OrganizerEventDetailsActivity.class);
        intent.putExtra("eventId", getIntent().getStringExtra("eventId"));
        startActivity(intent);
        finish();
    }
}
