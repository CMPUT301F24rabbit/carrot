package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * displays full list of users for admin to view
 */
public class AdminAllProfilesView extends AppCompatActivity {
    private ArrayList<User> dataUserList;
    private ListView userList;
    private ArrayAdapter<User> userArrayAdapter;
    private Button backBtn;
    private UserRepository userRepository;
    private ArrayList<DocumentSnapshot> userListFromDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_users);
        dataUserList = new ArrayList<>();
        userRepository = new UserRepository();

        // retrieve all users from firestore
        userList = findViewById(R.id.allUsersList);
        userRepository.getAllUsersFromFirestore(new UserRepository.FirestoreCallbackAllUsers() {
            @Override
            public void onSuccess(List<DocumentSnapshot> listOfUsers) {
                // cache list from firebase
                Log.i(TAG, "got all users!");
                userListFromDb = new ArrayList<>(listOfUsers);
                // add users from firebase to dataUserList
                getUsersFromFirestore(listOfUsers);
                // set data list in adapter
                userArrayAdapter = new UserArrayAdapter(AdminAllProfilesView.this, dataUserList);
                userList.setAdapter(userArrayAdapter);
            }
            @Override
            public void onFailure(Exception e) {
                // handle errors
                Log.i(TAG, "failed to get list of users");
            }
        });
        // open specific profile
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent;
                String userToViewId = userListFromDb.get(position).getId();
                intent = new Intent(AdminAllProfilesView.this, AdminProfileView.class);
                Log.i(TAG, "clicked on " + userToViewId);
                intent.putExtra("currentUserId", userToViewId);
                startActivity(intent);
            }
        });
        // back button navigates to admin home
        backBtn = findViewById(R.id.admin_all_users_back_btn);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminAllProfilesView.this, AdminHomeActivity.class);
            startActivity(intent);
        });
    }
    public void getUsersFromFirestore(List<DocumentSnapshot> listOfUsers) {
        // convert all documents into users
        for (int i = 0; i < listOfUsers.size(); i++) {
            try {
                DocumentSnapshot userFromDb = listOfUsers.get(i);
                User newUser = new UserImpl(userFromDb.getString("email"),
                        userFromDb.getString("userType"),
                        userFromDb.getString("name"), Optional.ofNullable(userFromDb.getString("phoneNumber")),
                        userFromDb.getBoolean("administratorNotification"),
                        userFromDb.getBoolean("organizerNotification"),
                        userFromDb.getString("userProfileImage")
                );
                // add user to user data list
                dataUserList.add(newUser);
                Log.i(TAG, "Successfully added " + userFromDb.getString("username"));
            } catch (Exception e) {
                Log.e(TAG, "Invalid user type, user not added");
            }
        }
    }
}