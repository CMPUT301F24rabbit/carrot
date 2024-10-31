package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;

public class OrganizerHomeView extends AppCompatActivity {

    private Button manageProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.organizer_home_view);

        manageProfileButton = findViewById(R.id.button_manage_profile);
        // Set an OnClickListener for the button
        manageProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ManageProfileActivity
                Intent intent = new Intent(OrganizerHomeView.this, OrganizerManageProfileActivity.class);
                startActivity(intent);
            }
        });
    }



}
