package com.example.goldencarrot.views;

import android.media.metrics.Event;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private RecyclerView upcomingEventsRecycle;
    private RecyclerView waitlistedEventsRecycle;
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
        upcomingEventsRecycle = findViewById(R.id.upcoming_events);
        waitlistedEventsRecycle = findViewById(R.id.waitlisted_events);
        addEventButton = findViewById(R.id.button_scanQR);

        // Set user name THIS IS A PLACEHOLDER FOR RIGHT NOW!!!!!
        usernameTextView.setText("Billy the Goat");

        // Add Event Button
        addEventButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This button takes the user to the QR scanner.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                // When Button Clicked Do:

                // Go to the add event

                //Intent intent = new Intent(EntrantHomeView.this, AddEventActivity.class);
                //startActivity(intent);
            }
        });

        // Load event data here
        loadEventData();
    }

    /**
     * Starting to think that this also goes into the controller
     */
    private void setupRecyclerViews(){
        // We want to set the layout for recycle view
        //upcomingEventsRecycle.setLayoutManager(new LinearLayoutManager(this));
        //waitlistedEventsRecycle.setLayoutManager(new LinearLayoutManager(this));

        // Now set the adapters to the recyclers
        // Adapters bind data
        // upcomingEventsRecycle.setAdapter(upcomingEventsAdapter);
        // waitlistedEventsRecycle.setAdapter(waitlistedEventsAdapter);
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
