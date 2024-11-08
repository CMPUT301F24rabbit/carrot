package com.example.goldencarrot.views;
import static com.example.goldencarrot.data.model.user.UserUtils.ACCEPTED_STATUS;
import static com.example.goldencarrot.data.model.user.UserUtils.WAITING_STATUS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.NotificationController;
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

        // Fetch the waitlist data
        waitListRepository.getWaitListByEventId(getIntent().getStringExtra("eventId"), new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                waitlistId = waitList.getWaitListId();
                fetchWaitlistedUsers(waitlistId);
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerWaitlistView.this, OrganizerEventDetailsActivity.class);
                intent.putExtra("eventId", getIntent().getStringExtra("eventId"));
                startActivity(intent);
                finish();
            }
        });

        // Set up send notification button click listener
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });
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
                    Log.d("OrganizerWaitlistView", "adding user: " + userId);
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
     * Sends a notification to all users in the waitlist, based on their status (waiting or accepted).
     */
    private void sendNotification() {
        if (getIntent().getStringExtra("entrantStatus").equals(WAITING_STATUS)) {
            for (String userId : userIdList) {
                userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                    @Override
                    public void onSuccess(UserImpl user) {
                        boolean userGetsNotif = user.getOrganizerNotifications();
                        if (userGetsNotif) {
                            createNotifForNonChosenUser(userId);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("OrganizerWaitlistView", "failed to get user");
                    }
                });
            }
        } else if (getIntent().getStringExtra("entrantStatus").equals(ACCEPTED_STATUS)) {
            for (String userId : userIdList) {
                userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                    @Override
                    public void onSuccess(UserImpl user) {
                        boolean userGetsNotif = user.getOrganizerNotifications();
                        if (userGetsNotif) {
                            createNotifForChosenUser(userId);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("OrganizerWaitlistView", "failed to get user");
                    }
                });
            }
        }
    }

    /**
     * Creates a notification for a user who has been chosen in the lottery.
     *
     * @param userId The ID of the chosen user.
     */
    private void createNotifForChosenUser(String userId) {
        notification = notifController.getOrCreateChosenNotification(
                userId,
                getIntent().getStringExtra("eventId"),
                waitlistId
        );
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
     * Creates a notification for a user who has not been chosen in the lottery.
     *
     * @param userId The ID of the non-chosen user.
     */
    private void createNotifForNonChosenUser(String userId) {
        notification = notifController.getOrCreateNotChosenNotification(
                userId,
                getIntent().getStringExtra("eventId"),
                waitlistId
        );
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
}
