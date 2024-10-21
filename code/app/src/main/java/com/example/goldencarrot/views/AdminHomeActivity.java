package com.example.goldencarrot.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;

public class AdminHomeActivity extends AppCompatActivity {
    private Button viewAllEventsButton, viewAllUsersButton, viewAllImagesButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        viewAllEventsButton = findViewById(R.id.adminAllEventsButton);
        viewAllUsersButton = findViewById(R.id.adminAllUsersButton);
        viewAllImagesButton = findViewById(R.id.adminAllImagesButton);

        viewAllEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewAllUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewAllImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
