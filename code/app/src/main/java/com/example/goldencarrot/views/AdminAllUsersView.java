package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.user.UserArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * displays full list of users
 */
public class AdminAllUsersView extends AppCompatActivity {
    private ArrayList<UserImpl> dataUserList;
    private ListView userList;
    private ArrayAdapter<UserImpl> userArrayAdapter;
    private FirebaseFirestore db;
    //private CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_users);
        dataUserList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        // retrieve all users from firestore
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserImpl nUser;
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                try {
                                    nUser = new UserImpl((String) document.getData().get("email"),
                                            (String) document.getData().get("userType"),
                                            (String) document.getData().get("username"));
                                    dataUserList.add(nUser);
                                } catch (Exception e) {

                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // setting listview with users array list
        userList = findViewById(R.id.allUsersList);
        userArrayAdapter = new UserArrayAdapter(this, dataUserList);
        userList.setAdapter(userArrayAdapter);
    }
}

