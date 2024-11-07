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

public class OrganizerWaitlistView extends AppCompatActivity {
    private static final String TAG = "OrganizerWaitlistView";

    private RecyclerView recyclerView;
    private WaitlistUsersAdapter adapter;
    private ArrayList<User> waitlist = new ArrayList<>();
    private FirebaseFirestore db;
    private String waitlistType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitlist_users_list);

        // Retrieve the waitlist type (waitlisted, accepted, or declined)
        waitlistType = getIntent().getStringExtra("entrantStatus");
        if (waitlistType != null) {
            setTitle(waitlistType.substring(0, 1).toUpperCase() + waitlistType.substring(1) + " Participants");
        }

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.waitlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with waitlist data and set it on the RecyclerView
        adapter = new WaitlistUsersAdapter(waitlist, waitlistType);
        recyclerView.setAdapter(adapter);

        // Load the waitlist based on the type (waitlisted, accepted, declined)
        loadWaitlist();
    }

    /**
     * Loads the waitlist from Firestore based on the event ID and waitlist type.
     */
    private void loadWaitlist() {
        // Get the event ID from the intent
        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null) {
            Log.e(TAG, "Event ID not found.");
            return;
        }

        // Reference to the appropriate waitlist collection for the event
        CollectionReference waitlistRef = db.collection("events").document(eventID).collection(waitlistType);

        // Listen for changes in the waitlist collection
        waitlistRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Error fetching " + waitlistType + " users", e);
                    return;
                }

                // Clear the current waitlist to avoid duplicates
                waitlist.clear();

                // Populate the waitlist from Firestore documents
                if (queryDocumentSnapshots != null) {
                    queryDocumentSnapshots.forEach(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        waitlist.add(user);
                    });

                    // Notify adapter to update the RecyclerView
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
