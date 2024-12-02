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

 /**
 * Activity that displays a poster image in full-screen mode and allows the user to delete it.
 *
 * <p> This activity allows users to view a poster image in full-screen, go back to the poster gallery,
 * and delete the image from Firebase Storage. When the image is deleted successfully, the poster gallery
 * is updated, and the list of poster URLs is passed back to the AdminHomeActivity.
 */
public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;
    private Button backButton, deleteButton;
    private String imageUrl;

    /**
     * Called when the activity is created.
     *
     * <p> This method initializes the UI components, retrieves the image URL passed through the intent,
     * loads the image into the ImageView, and sets up the back and delete buttons' functionality.
     *
     * @param savedInstanceState The saved instance state.
     */
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

    /**
     * Deletes the poster image from Firebase Storage and updates the list of posters.
     *
     * <p> This method attempts to decode the image URL, extract the image name, and delete the corresponding
     * image file from Firebase Storage. Upon successful deletion, the updated poster list is fetched and passed
     * to the AdminHomeActivity.
     */
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

    /**
     * Retrieves the updated list of poster URLs from Firebase Storage.
     *
     * <p> This method fetches the list of posters stored in Firebase Storage, retrieves their download URLs,
     * and adds them to a list. Once all URLs are retrieved, the list is passed to the AdminHomeActivity.
     *
     * @return A list of updated poster URLs.
     */
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

    /**
     * Redirects to the AdminHomeActivity and passes the updated list of poster URLs.
     *
     * @param posterUrls The updated list of poster URLs to pass to the AdminHomeActivity.
     */
    private void redirectToAdminHomeActivity(List<String> posterUrls) {
        // Intent to navigate to AdminHomeActivity and pass the updated list of posters (if needed)
        Intent intent = new Intent(FullScreenImageActivity.this, AdminHomeActivity.class);
        intent.putStringArrayListExtra("posterUrls", new ArrayList<>(posterUrls));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  // Bring the AdminHomeActivity to the front
        startActivity(intent);
    }
}
