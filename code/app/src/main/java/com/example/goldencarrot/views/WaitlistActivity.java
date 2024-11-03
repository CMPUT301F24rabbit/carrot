package com.example.goldencarrot.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;

public class WaitlistActivity extends AppCompatActivity {

    private ListView waitingListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> waitlist;

    // Firestore database reference
    private FirebaseFirestore firestore;
    private CollectionReference waitlistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_waitlist);

        // Initialize the ListView
        waitingListView = findViewById(R.id.waitingListView);

        // Initialize the waitlist ArrayList
        waitlist = new ArrayList<>();

        // Set up the custom adapter with ArrayAdapter using the custom view
        adapter = new ArrayAdapter<String>(this, R.layout.entrant_waitlist_item, R.id.EventNameTextView, waitlist) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.entrant_waitlist_item, parent, false);
                }

                // Get the event name TextView and set the event name
                TextView eventNameTextView = convertView.findViewById(R.id.EventNameTextView);
                eventNameTextView.setText(getItem(position));

                // Set up leave button to remove participant from waitlist in Firestore
                Button leaveButton = convertView.findViewById(R.id.leaveButton);
                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String eventToRemove = getItem(position);
                        // Remove item from Firestore and update the ListView
                        removeEventFromWaitlist(eventToRemove, position);
                    }
                });

                return convertView;
            }
        };

        // Set the adapter to the ListView
        waitingListView.setAdapter(adapter);

        // Initialize Firestore and reference to the "waitlist" collection
        firestore = FirebaseFirestore.getInstance();
        waitlistRef = firestore.collection("waitlist");

        // Load data from Firestore
        loadWaitlistData();
    }

    private void loadWaitlistData() {
        waitlistRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                waitlist.clear(); // Clear existing data
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String event = document.getString("eventName"); // Assumes each document has an "eventName" field
                    if (event != null) {
                        waitlist.add(event);
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh the adapter
            } else {
                Log.e("WaitlistActivity", "Error getting waitlist data", task.getException());
            }
        });
    }

    private void removeEventFromWaitlist(String eventToRemove, int position) {
        // Query to find and remove the specified event
        waitlistRef.whereEqualTo("eventName", eventToRemove).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete() // Remove the item from Firestore
                                    .addOnSuccessListener(aVoid -> {
                                        waitlist.remove(position); // Remove from local list
                                        adapter.notifyDataSetChanged(); // Refresh the ListView
                                    })
                                    .addOnFailureListener(e -> Log.e("WaitlistActivity", "Error removing event", e));
                        }
                    } else {
                        Log.e("WaitlistActivity", "Error finding event to remove", task.getException());
                    }
                });
    }
}
