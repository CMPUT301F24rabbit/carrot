package com.example.goldencarrot.views;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that handles all entrant app features
 */
public class EntrantHomeView extends AppCompatActivity {

    // Initialize variables
    private TextView usernameTextView;
    private ImageView profileImageView;
    private ScrollView upcomingEventsScroll;
    private ScrollView waitlistedEventsScroll;
    private Button addEventButton;

    // Initialize Adapters for Recycler Views
    //private EventAdapter upcomingEventsAdapter;
    //private EventAdapter waitlistedEventsAdapter;

    private List<Event> upcomingEventsList = new ArrayList<>();
    private List<Event> waitlistedEventsList = new ArrayList<>();

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.entrant_home_view);

        // Initialize the views from layout file
        profileImageView = findViewById(R.id.entrant_home_view_image_view);
        usernameTextView = findViewById(R.id.entrant_home_view_user_name);
        upcomingEventsScroll = findViewById(R.id.upcoming_events);
        waitlistedEventsScroll = findViewById(R.id.waitlisted_events);
        addEventButton = findViewById(R.id.button_explore_events);

        // Set user name THIS IS A PLACEHOLDER FOR RIGHT NOW!!!!!
        usernameTextView.setText("Billy the Goat");

        // Load event data here
        loadEventData();

       /** upcomingEventsScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the upcoming events for a Entrant to do stuff
                Intent intent = new Intent(EntrantHomeView.this, AcceptedListActivity.class);
                startActivity(intent;);
            }
        });**/

        waitlistedEventsScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntrantHomeView.this, WaitlistActivity.class);
                startActivity(intent);
            }
        });

        // Add Event Button
        addEventButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This button takes the user to the QR scanner.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                // When Button Clicked Do: Go to Events Exploration Activity

                // Go to the add event

                //Intent intent = new Intent(EntrantHomeView.this, AddEventActivity.class);
                //startActivity(intent);
            }
        });


    }

    /**
     * Starting to think this goes in the controller?
     */
    private void loadEventData(){
        // I want to populate the Entrants upcoming events

        // I want to populate the Entrants waitlisted events

        // Notify the adapters that a change has occurred

    }
}
