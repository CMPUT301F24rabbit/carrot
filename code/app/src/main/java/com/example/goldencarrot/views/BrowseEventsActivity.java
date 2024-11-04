package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private ArrayList<DocumentSnapshot> eventDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Initialize Firestore and Collections
        firestore = FirebaseFirestore.getInstance();
        eventsCollection = firestore.collection("events");

        // Initialize ListView and Adapter
        eventsListView = findViewById(R.id.eventsListView);
        eventsList = new ArrayList<>();
        eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsList);
        eventsListView.setAdapter(eventsAdapter);

        // Initialize list for event documents
        eventDocuments = new ArrayList<>();

        // Fetch events from Firestore
        loadEventsFromFirestore();

        // Set an item click listener to open EventDetailsActivity
        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected event document
            DocumentSnapshot selectedDocument = eventDocuments.get(position);
            String documentId = selectedDocument.getId();

            // Start EventDetailsActivity and pass document ID as an extra
            Intent intent = new Intent(BrowseEventsActivity.this, EventDetailsActivity.class);
            intent.putExtra("documentId", documentId);
            startActivity(intent);
        });
    }

    private void loadEventsFromFirestore() {
        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventsList.clear();
                eventDocuments.clear();
                QuerySnapshot querySnapshot = task.getResult();

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String eventName = document.getString("eventName");
                        eventsList.add(eventName);
                        eventDocuments.add(document);  // Add the document for later retrieval
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
