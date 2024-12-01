package com.example.goldencarrot.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;
    private Button backButton, deleteButton;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        // Initialize UI components
        fullScreenImageView = findViewById(R.id.fullScreenImageView);
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get the image URL from the intent
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("imageUrl");

        // Use Picasso to load the image from the URL into the ImageView
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(fullScreenImageView);
        }

        // Set up the back button
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(FullScreenImageActivity.this, AdminPosterGalleryActivity.class);
            backIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(backIntent);
        });

        // Set up the delete button
        deleteButton.setOnClickListener(v -> deletePoster());
    }
    private void deletePoster() {
        @SuppressLint({"NewApi", "LocalSuppress"})
        String decodedUrl = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
        String imageName = decodedUrl.substring(decodedUrl.lastIndexOf("/") + 1, decodedUrl.indexOf("?"));
        String posterPath = "posters/" + imageName;

        // Get reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(posterPath);
        Log.d("DeletePoster", "Trying to delete: " + posterPath);

        // Delete the image from Firebase Storage
        imageRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(FullScreenImageActivity.this, "Poster deleted successfully.", Toast.LENGTH_SHORT).show();

            // After successful deletion, fetch updated list of poster URLs
            List<String> updatedPosterUrls = getUpdatedPosterList(); // Get the updated list of URLs

            // Pass the updated list back to AdminHomeActivity (instead of AdminPosterGalleryActivity)
            redirectToAdminHomeActivity(updatedPosterUrls);
        }).addOnFailureListener(e -> {
            Toast.makeText(FullScreenImageActivity.this, "Failed to delete poster: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private List<String> getUpdatedPosterList() {
        // Placeholder for logic to fetch updated poster list from Firebase Storage or Firestore
        List<String> updatedList = new ArrayList<>();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("posters");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    updatedList.add(uri.toString());

                    // Once all URLs are fetched, update the gallery (or redirect to HomeActivity if needed)
                    if (updatedList.size() == listResult.getItems().size()) {
                        redirectToAdminHomeActivity(updatedList); // Update the gallery and navigate to AdminHomeActivity
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Error getting download URL: " + e.getMessage());
                });
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase", "Error listing storage items: " + e.getMessage());
        });

        return updatedList;
    }

    private void redirectToAdminHomeActivity(List<String> posterUrls) {
        // Intent to navigate to AdminHomeActivity and pass the updated list of posters (if needed)
        Intent intent = new Intent(FullScreenImageActivity.this, AdminHomeActivity.class);
        intent.putStringArrayListExtra("posterUrls", new ArrayList<>(posterUrls));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  // Bring the AdminHomeActivity to the front
        startActivity(intent);
    }
}