package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * displays full list of users
 */
public class AdminAllUsersView extends AppCompatActivity {
    private ArrayList<User> dataUserList;
    private ListView userList;
    private ArrayAdapter<User> userArrayAdapter;
    private FirebaseFirestore db;
    //private CollectionReference userCollection;
    private UserRepository userRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_users);
        dataUserList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository();

        // retrieve all users from firestore
        userList = findViewById(R.id.allUsersList);
        userRepository.getAllUsersFromFirestore(new UserRepository.FirestoreCallbackAllUsers() {
            @Override
            public void onSuccess(List<DocumentSnapshot> listOfUsers) {
                // convert all documents into users
                for (int i = 0; i < listOfUsers.size(); i++) {
                    try {
                        DocumentSnapshot userFromDb = listOfUsers.get(i);
                        User newUser = new UserImpl(userFromDb.getString("email"),
                                userFromDb.getString("userType"),
                                userFromDb.getString("username"));
                        // add user to user data list
                        dataUserList.add(newUser);
                        Log.i(TAG, "Successfully added " + userFromDb.getString("username"));
                    } catch (Exception e) {
                        Log.i(TAG, "Invalid user type, user not added");
                    }
                }

                // set data list in adapter
                userArrayAdapter = new UserArrayAdapter(AdminAllUsersView.this, dataUserList);
                userList.setAdapter(userArrayAdapter);
            }
            @Override
            public void onFailure(Exception e) {
                // handle errors
                Log.i(TAG, "failed to get list of users");
            }
        });
    }
}

