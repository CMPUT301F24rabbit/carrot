package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.RanBackground;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Map;
/**
 * This activity displays the user's waitlist for events. The user can view the events they are waiting for and remove themselves
 * from the waitlist. The waitlist data is fetched from Firestore, and any changes (such as removing a user from the waitlist)
 * are reflected in real-time.
 */
public class WaitlistActivity extends AppCompatActivity {

    // ListView for displaying the waitlist of events
    private ListView waitingListView;

    // Adapter for the ListView to display event names
    private ArrayAdapter<String> adapter;

    // List to store event names for the waitlist
    private ArrayList<String> waitlist;

    // Firestore database reference to access the "waitlist" collection
    private FirebaseFirestore firestore;
    private CollectionReference waitlistRef;

    /**
     * Called when the activity is created. Sets up the UI components, initializes Firestore,
     * and loads the user's waitlist data.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_waitlist);

        // Apply RNG Background
        LinearLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));

        // Initialize ListView and ArrayList
        waitingListView = findViewById(R.id.waitingListView);
        waitlist = new ArrayList<>();

        // Set up back button to return to the previous activity
        Button backButton = findViewById(R.id.button_back_to_previous_activity);
        backButton.setOnClickListener(v -> finish());

        // Initialize the adapter with custom layout for each list item
        adapter = new ArrayAdapter<String>(this, R.layout.entrant_waitlist_item, R.id.EventNameTextView, waitlist) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.entrant_waitlist_item, parent, false);
                }

                // Set the event name text in the ListView item
                TextView eventNameTextView = convertView.findViewById(R.id.EventNameTextView);
                eventNameTextView.setText(getItem(position));

                // Set up the leave button to remove the user from the waitlist
                Button leaveButton = convertView.findViewById(R.id.leaveButton);
                leaveButton.setOnClickListener(v -> {
                    String eventToRemove = getItem(position);
                    removeEventFromWaitlist(eventToRemove, position);
                });

                return convertView;
            }
        };

        // Set the adapter to the ListView
        waitingListView.setAdapter(adapter);

        // Initialize Firestore and reference the "waitlist" collection
        firestore = FirebaseFirestore.getInstance();
        waitlistRef = firestore.collection("waitlist");

        // Load the waitlist data from Firestore
        loadWaitlistData();
    }

    /**
     * Loads the user's waitlist data from Firestore. It queries the "waitlist" collection
     * and updates the local `waitlist` ArrayList with events the user is waiting for.
     */
    private void loadWaitlistData() {
        waitlistRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                waitlist.clear(); // Clear existing data
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> usersMap = (Map<String, Object>) document.get("users");
                    if (usersMap.containsKey(getDeviceId(this))) {
                        String event = document.getString("eventName"); // Get event name
                        if (event != null) {
                            waitlist.add(event);
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh the adapter to display the updated list
            } else {
                Log.e("WaitlistActivity", "Error getting waitlist data", task.getException());
            }
        });
    }

    /**
     * Retrieves the Android device ID for the current device.
     *
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Removes the specified event from the user's waitlist in Firestore and updates the local ListView.
     *
     * @param eventToRemove The event name to remove from the waitlist.
     * @param position The position of the event in the ListView.
     */
    private void removeEventFromWaitlist(String eventToRemove, int position) {
        String deviceId = getDeviceId(this);

        // Query to find and remove the specified event
        waitlistRef.whereEqualTo("eventName", eventToRemove).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().update("users." + deviceId, FieldValue.delete()) // Remove the user from Firestore
                                    .addOnSuccessListener(aVoid -> {
                                        waitlist.remove(position); // Remove from the local waitlist
                                        adapter.notifyDataSetChanged(); // Refresh the ListView
                                        Log.d("WaitlistActivity", "User successfully removed from waitlist for event " + eventToRemove);

                                        // Return to the home view after removal
                                        Intent intent = new Intent(WaitlistActivity.this, EntrantHomeView.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> Log.e("WaitlistActivity", "Error removing event", e));
                        }
                    } else {
                        Log.e("WaitlistActivity", "Error finding event to remove", task.getException());
                    }
                });
    }
}
