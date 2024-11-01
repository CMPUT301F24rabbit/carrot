package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;

/**
 * This is a class that handles all entrant app features.
 */
public class EntrantHomeView extends AppCompatActivity {

    // Initialize variables
    private TextView usernameTextView;
    private ImageView profileImageView;
    private ScrollView upcomingEventsScroll;
    private ScrollView waitlistedEventsScroll;
    private Button addEventButton;
    private Button goToWaitlistButton;

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
        goToWaitlistButton = findViewById(R.id.button_go_to_waitlist);

        // Set user name as a placeholder
        usernameTextView.setText("Billy the Goat");

        // Set the click listener for the waitlist button
        goToWaitlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the WaitlistActivity
                Intent intent = new Intent(EntrantHomeView.this, WaitlistActivity.class);
                startActivity(intent);
            }
        });

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

    // Placeholder method to load event data
    private void loadEventData() {
        // Load the Entrant's upcoming events and waitlisted events
    }
}
