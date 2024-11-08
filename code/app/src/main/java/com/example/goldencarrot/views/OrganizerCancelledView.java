package com.example.goldencarrot.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Activity that displays the list of users who have cancelled from a waitlist for an event.
 * The activity retrieves the event's waitlist, fetches users with a "cancelled" status,
 * and displays them in a RecyclerView.
 */
public class OrganizerCancelledView extends AppCompatActivity {

    private ArrayList<String> userIdList;
    private RecyclerView cancelledUserListView;
    private WaitlistedUsersRecyclerAdapter userArrayAdapter;
    private FirebaseFirestore db;
    private Button backBtn;
    private WaitListRepository waitListRepository;
    private UserRepository userRepository;
    private ArrayList<User> cancelledUserList;
    private WaitList eventWaitlist;
    private String waitlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_user_list);

        // Initialize the lists
        userIdList = new ArrayList<>();
        cancelledUserList = new ArrayList<>();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        waitListRepository = new WaitListRepository();
        userRepository = new UserRepository();

        // Initialize layout views
        cancelledUserListView = findViewById(R.id.cancelledUsersRecyclerView);

        // Get the waitlist of the event
        waitListRepository.getWaitListByEventId(getIntent().getStringExtra("eventId"), new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                eventWaitlist = waitList;
                Log.d("OrganizerCancelledView", "Got waitlist!");
                waitlistId = eventWaitlist.getWaitListId();

                // Fetch users with "cancelled" status after getting waitlistId
                fetchCancelledUsers();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerCancelledView", "Failed to get waitlist");
            }
        });
    }

    /**
     * Fetches the list of user IDs from the waitlist that have a "cancelled" status.
     */
    private void fetchCancelledUsers() {
        // Get user IDs with "cancelled" status from the waitlist
        waitListRepository.getUsersWithStatus(waitlistId, "cancelled", new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                userIdList = (ArrayList<String>) result;
                Log.d("OrganizerCancelledView", "Cancelled user IDs retrieved.");

                // Fetch User details for each ID
                fetchUserDetails();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerCancelledView", "Failed to get cancelled user list");
            }
        });
    }

    /**
     * Fetches user details for each user ID with "cancelled" status.
     * Adds each user to the cancelled user list and updates the RecyclerView.
     */
    private void fetchUserDetails() {
        for (String userId : userIdList) {
            userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                @Override
                public void onSuccess(UserImpl user) {
                    cancelledUserList.add(user);
                    Log.d("OrganizerCancelledView", "Added user to list: " + user.getName());

                    // Notify the adapter that data has changed after each addition
                    userArrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("OrganizerCancelledView", "Failed to get user from Firestore");
                }
            });
        }

        // Set up the adapter once and attach it to the RecyclerView
        userArrayAdapter = new WaitlistedUsersRecyclerAdapter(cancelledUserList);
        cancelledUserListView.setAdapter(userArrayAdapter);
    }
}
