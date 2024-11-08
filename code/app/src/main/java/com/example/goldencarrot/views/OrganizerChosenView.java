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
import com.example.goldencarrot.utils.FirestoreCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays the list of users who have been chosen by lottery from a waitlist for an event.
 * The activity retrieves the event's waitlist, fetches users with a "chosen" status,
 * and displays them in a RecyclerView.
 */
public class OrganizerChosenView extends AppCompatActivity {

    private ArrayList<String> userIdList;
    private RecyclerView chosenUserListView;
    private WaitlistedUsersRecyclerAdapter userArrayAdapter;
    private FirebaseFirestore db;
    private Button backBtn;
    private WaitListRepository waitListRepository;
    private UserRepository userRepository;
    private ArrayList<User> chosenUserList;
    private WaitList eventWaitlist;
    private String waitlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_users_list);

        // Initialize the lists
        userIdList = new ArrayList<>();
        chosenUserList = new ArrayList<>();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        waitListRepository = new WaitListRepository();
        userRepository = new UserRepository();

        // Initialize layout views
        chosenUserListView = findViewById(R.id.chosenUsersRecyclerView);

        // Get the waitlist of the event
        waitListRepository.getWaitListByEventId(getIntent().getStringExtra("eventId"), new FirestoreCallback<WaitList>() {
            @Override
            public void onSuccess(WaitList result) {
                eventWaitlist = result;
                Log.d("OrganizerChosenView", "Got waitlist!");
                waitlistId = eventWaitlist.getWaitListId();

                // Fetch users
                fetchChosenUsers();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerChosenView", "Failed to get waitlist");
            }
        });
    }

    /**
     * Fetches the list of user IDs from the waitlist that have a "chosen" status.
     */
    private void fetchChosenUsers() {
        // Get user IDs with "chosen" status from the waitlist
        waitListRepository.getUsersWithStatus(waitlistId, "chosen", new FirestoreCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                userIdList = new ArrayList<>(result);
                Log.d("OrganizerChosentView", "Chosen user ID's retrieved.");

                // Fetch user details foreach ID
                fetchUserDetails();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerChosenView", "Failed to get chosen user list");
            }
        });
    }

    /**
     * Fetches user details for each user ID with "chosen" status.
     * Adds each user to the chosen user list and updates the RecyclerView.
     */
    private void fetchUserDetails() {
        for (String userId : userIdList) {
            userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                @Override
                public void onSuccess(UserImpl user) {
                    chosenUserList.add(user);
                    Log.d("OrganizerChosenView", "Added user to list: " + user.getName());

                    // Notify the adapter that data has changed after each addition
                    userArrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("OrganizerChosenView", "Failed to get user from Firestore");
                }
            });
        }

        // Set up the adapter once and attach it to the RecyclerView
        userArrayAdapter = new WaitlistedUsersRecyclerAdapter(chosenUserList);
        chosenUserListView.setAdapter(userArrayAdapter);
    }
}
