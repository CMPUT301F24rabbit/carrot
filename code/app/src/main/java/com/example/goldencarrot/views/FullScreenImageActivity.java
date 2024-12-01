package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
    private ImageView fullScreenImageView;
    private Button backButton;  // Changed to Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        // Initialize the ImageView for full-screen display
        fullScreenImageView = findViewById(R.id.fullScreenImageView);
        backButton = findViewById(R.id.backButton);  // Button initialization

        // Get the image URL from the intent
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");

        // Use Picasso to load the image from the URL into the ImageView
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(fullScreenImageView);
        }

        // Set up the back button click listener
        backButton.setOnClickListener(v -> {
            // Return to AdminPosterGalleryActivity
            Intent backIntent = new Intent(FullScreenImageActivity.this, AdminPosterGalleryActivity.class);
            backIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(backIntent);
        });
    }
}
