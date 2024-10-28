package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;

public class EntrantHomeView extends AppCompatActivity {
    private ScrollView waitlistedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_home_view);

        // Reference the waitlisted events scroll view from the XML
        waitlistedEvents = findViewById(R.id.waitlisted_events);

        // User Details Button, sends user to event details page
        findViewById(R.id.entrant_home_view_user_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantHomeView.this, EntrantEditUserDetailsView.class);
                startActivity(intent);
            }
        });

        // Set an onClickListener to handle the scroll view click
        waitlistedEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the WaitlistActivity when clicked
                Intent intent = new Intent(EntrantHomeView.this, WaitlistActivity.class);
                startActivity(intent);
            }
        });
    }
}