package com.example.goldencarrot.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class OrganizerApprovedView extends AppCompatActivity {
    private static final String TAG = "OrganizerChosenView";

    private RecyclerView recyclerView;
    private CancelledUsersAdapter adapter;
    private ArrayList<User> ApprovedUsersList = new ArrayList<>();
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_users_list);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.approvedUsersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CancelledUsersAdapter(ApprovedUsersList);
        recyclerView.setAdapter(adapter);

        // Load Cancelled Users
        loadApprovedUsers();
    }

    private void loadApprovedUsers() {
        // Assume eventID is passed to this activity
        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null) {
            Log.e(TAG, "Event ID not found.");
            return;
        }

        // Reference to the ApprovedUsers collection for the specific event
        CollectionReference ApprovedUsersRef = db.collection("events").document(eventID).collection("ApprovedUsers");

        // Listen for changes in the cancelledUsers collection
        ApprovedUsersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Error fetching chosen users", e);
                    return;
                }

                ApprovedUsersList.clear(); // Clear the list before updating it

                // Populate the list with data from Firebase
                if (queryDocumentSnapshots != null) {
                    queryDocumentSnapshots.forEach(documentSnapshot -> {
                        UserImpl user = documentSnapshot.toObject(UserImpl.class);
                        ApprovedUsersList.add(user);
                    });

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


}
