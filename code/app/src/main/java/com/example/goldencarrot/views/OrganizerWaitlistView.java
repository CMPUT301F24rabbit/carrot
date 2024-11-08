package com.example.goldencarrot.views;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlisted_users_list);

        userIdList = new ArrayList<>();
        waitlistedUserList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        waitListRepository = new WaitListRepository();
        userRepository = new UserRepository();

        // setting up layout views
        listTitle = findViewById(R.id.entrantsListTitle);
        waitlistedUserListView = findViewById(R.id.waitlistedUsersList);
        Button backBtn = findViewById(R.id.backButtonFromWaitlist);

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

        userArrayAdapter = new UserArrayAdapter(this, waitlistedUserList);
        waitlistedUserListView.setAdapter(userArrayAdapter);

        // set title
        listTitle.setText(getIntent().getStringExtra("entrantStatus").toUpperCase());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerWaitlistView.this, OrganizerEventDetailsActivity.class);
                intent.putExtra("eventId", getIntent().getStringExtra("eventId"));
                startActivity(intent);
                finish();
            }
        });
    }

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
}
