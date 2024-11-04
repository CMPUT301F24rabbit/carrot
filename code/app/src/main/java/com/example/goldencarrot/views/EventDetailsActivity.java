package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Set up back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Retrieve and display event details
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventName");
        String eventDetails = intent.getStringExtra("eventDetails");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");

        TextView eventDetailsTextView = findViewById(R.id.eventDetailsTextView);
        eventDetailsTextView.setText(String.format("Event Name: %s\nEvent Details: %s\nLocation: %s\nDate: %s",
                eventName, eventDetails, location, date));
    }
}
