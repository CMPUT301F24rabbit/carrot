package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.event.EventArrayAdapter;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

/**
 * This is a class that handles all entrant app features.
 */
public class EntrantHomeView extends AppCompatActivity {

    // Initialize UI
    private TextView usernameTextView;
    private TextView waitlistedEventsTitle;
    private ImageView profileImageView;
    private ListView upcomingEventsListView;
    private ListView waitlistedEventsListView;
    private Button addEventButton;
    private Button goToWaitlistButton;

   // Firestore
    private FirebaseFirestore firestore;
    private EventArrayAdapter upcomingEventsAdapter;
    private EventArrayAdapter waitlistedEventsAdapter;
    private ArrayList<Event> upcomingEventsList;
    private ArrayList<Event> waitlistedEventsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_home_view);

        // Initialize the views from layout file
        profileImageView = findViewById(R.id.entrant_home_view_image_view);
        usernameTextView = findViewById(R.id.entrant_home_view_user_name);
        upcomingEventsListView = findViewById(R.id.upcoming_events);
        waitlistedEventsListView = findViewById(R.id.waitlisted_events);
        addEventButton = findViewById(R.id.button_explore_events);
        waitlistedEventsTitle = findViewById(R.id.waitlisted_events_title);

        // Set user name as a placeholder
        usernameTextView.setText("Billy the Goat");

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Event lists and adapter Inititalization
        upcomingEventsList =new ArrayList<>();
        waitlistedEventsList =  new ArrayList<>();
        upcomingEventsAdapter = new EventArrayAdapter(this, upcomingEventsList);
        waitlistedEventsAdapter = new EventArrayAdapter(this, waitlistedEventsList);

        // Set adapters to listview
        upcomingEventsListView.setAdapter(upcomingEventsAdapter);
        waitlistedEventsListView.setAdapter(waitlistedEventsAdapter);

        // Open WaitlistActivity
        setListenersForWaitlistActivity(waitlistedEventsListView, waitlistedEventsTitle, WaitlistActivity.class);

        // Do the same thing for upcoming events when we get that far

        // Set the click listener for the "Explore Events" button (add event functionality)
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to Events Exploration Activity
                // Intent intent = new Intent(EntrantHomeView.this, AddEventActivity.class);
                // startActivity(intent);
            }
        });

        // Load event data
        loadEventData();
    }

    private void setListenersForWaitlistActivity(ListView listView, TextView titleView, Class<?> activityClass){
        // Open when title is clicked
        titleView.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantHomeView.this, activityClass);
            startActivity(intent);
        });

        // Open when List view is long clicked
        listView.setOnItemLongClickListener((parent, view, position, id) ->{
            Intent intent = new Intent(EntrantHomeView.this, activityClass);
            startActivity(intent);
            return true;
        });
    }

    // Placeholder method to load event data
    private void loadEventData() {
        // Load the first 4 waitlisted events from firestore
        firestore.collection("waitlistedEvents")
                .orderBy("date")
                .limit(4) // We only want the first 4 to show up
                .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                waitlistedEventsList.clear(); // clear the current data
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Get Event Data
                                    String eventName = document.getString("eventName");
                                    String location = document.getString("location");
                                    String details = document.getString("details");
                                    Date eventDate = document.getDate("date");
                                    UserImpl organizer = document.toObject(UserImpl.class);

                                    // Make the Event  Object and set  details
                                    Event event = new Event(organizer);
                                    event.setOrganizer(organizer);
                                    event.setEventName(eventName);
                                    event.setLocation(location);
                                    event.setEventDetails(details);
                                    event.setDate(eventDate);

                                    // Now addevent to thelist
                                    waitlistedEventsList.add(event);
                                }
                                // Notify adapter if changes
                                waitlistedEventsAdapter.notifyDataSetChanged();
                            }
                            else {
                                // error stuff
                                Log.e("EntrantHomeView",  "Error loading waitlisted events", task.getException());
                            }

                        });
    }
}
