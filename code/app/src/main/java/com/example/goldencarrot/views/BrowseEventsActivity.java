package com.example.goldencarrot.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseEventsActivity extends AppCompatActivity {
    private static final String TAG = "BrowseEventsActivity";

    private FirebaseFirestore firestore;
    private CollectionReference eventsCollection;
    private ListView eventsListView;
    private ArrayAdapter<String> eventsAdapter;
    private ArrayList<String> eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        eventsCollection = firestore.collection("events");

        // Initialize ListView and Adapter
        eventsListView = findViewById(R.id.eventsListView);
        eventsList = new ArrayList<>();
        eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsList);
        eventsListView.setAdapter(eventsAdapter);

        // Fetch events from Firestore
        loadEventsFromFirestore();
    }

    private void loadEventsFromFirestore() {
        eventsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventsList.clear();
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Assuming each document has a "name" field for event name
                                String eventName = document.getString("eventName");
                                eventsList.add(eventName);
                            }
                            eventsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
