package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;

public class AdminHomeActivity extends AppCompatActivity {
  
    private Button adminViewAllEventsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        // View events button
        adminViewAllEventsButton = findViewById(R.id.adminViewAllEvents);

        adminViewAllEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open BrowseEventsActivity when the events button is clicked
                Intent intent = new Intent(AdminHomeActivity.this, BrowseEventsActivity.class);
                startActivity(intent);
            }
        });
    }

}
