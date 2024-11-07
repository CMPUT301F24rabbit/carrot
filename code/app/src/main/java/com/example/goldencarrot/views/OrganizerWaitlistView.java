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
 * display list of all users in waitlist
 */
public class OrganizerWaitlistView extends AppCompatActivity {
    private ArrayList<String> userIdList;
    private RecyclerView waitlistedUserListView;
    private WaitlistedUsersRecyclerAdapter userArrayAdapter;
    private FirebaseFirestore db;
    private Button backBtn;
    private WaitListRepository waitListRepository;
    private UserRepository userRepository;
    private ArrayList<User> waitlistedUserList;
    private WaitList eventWaitlist;
    private String waitlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlisted_users_list);

        // initialize id list of chosen users
        userIdList = new ArrayList<>();
        waitlistedUserList = new ArrayList<>();

        // initialize firestore
        db = FirebaseFirestore.getInstance();
        waitListRepository = new WaitListRepository();
        userRepository = new UserRepository();

        // initialize layout views
        waitlistedUserListView = findViewById(R.id.waitlistedUsersRecyclerView);

        // get waitlist of event
        waitListRepository.getWaitListByEventId(getIntent().getStringExtra("eventId"), new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                eventWaitlist = waitList;
                Log.d("OrganizerWaitlistView", "got waitlist!");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerWaitlistView", "Failed to get waitlist");
            }
        });
        waitlistId = eventWaitlist.getWaitListId();
        Log.d("OrganizerWaitlistView", "waitlist id: " + waitlistId);

        // getting array of user ids with "chosen" status
        waitListRepository.getUsersWithStatus(waitlistId, "waiting", new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                userIdList = (ArrayList<String>) result;
                for (String userId : userIdList) {
                    Log.d("OrganizerWaitlistView", "waitlisted user: " + userId);
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.d("OrganizerWaitlistView", "failed to get user list");
            }
        });
        for (String userId : userIdList) {
            userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
                @Override
                public void onSuccess(UserImpl user) {
                    waitlistedUserList.add(user);
                    Log.d("OrganizerWaitlistView", "added user to list: " + user.getName());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("OrganizerWaitlistView", "failed to get user from firestore");
                }
            });
        }
        userArrayAdapter = new WaitlistedUsersRecyclerAdapter(waitlistedUserList);
        waitlistedUserListView.setAdapter(userArrayAdapter);
    }
}
