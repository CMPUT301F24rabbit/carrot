package com.example.goldencarrot.views;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Optional;

/**
 * Activity that displays an admin view of a user's profile.
 * The admin can view user details, delete the profile, or view the facility profile if applicable.
 */
public class AdminProfileView extends AppCompatActivity {
    private String userId;
    private FirebaseFirestore db;
    private ImageView profileImageView;
    private Button backBtn, deleteBtn, facilityProfileBtn;
    private TextView nameText, emailText, userTypeText, phoneNumberText;
    private UserRepository userRepository;

    /**
     * Called when the activity is created. Initializes the UI components, loads the user's profile data,
     * and sets up button click listeners for back, delete, and facility profile actions.
     *
     * @param savedInstanceState the saved state of the activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_views_profile);

        db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository();
        // Extract user ID from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("currentUserId");
        }

        nameText = findViewById(R.id.profileNameText);
        emailText = findViewById(R.id.profileEmailText);
        userTypeText = findViewById(R.id.profileUserTypeText);
        phoneNumberText = findViewById(R.id.profilePhoneNumber);
        profileImageView = findViewById(R.id.adminProfileImageView);
        facilityProfileBtn = findViewById(R.id.viewFacilityProfileBtn);

        // Load the user profile data
        loadProfileData();

        // Back button listener
        backBtn = findViewById(R.id.adminViewProfileBackBtn);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminProfileView.this, AdminAllProfilesView.class);
            startActivity(intent);
        });

        // Delete button listener
        deleteBtn = findViewById(R.id.deleteProfileBtn);
        deleteBtn.setOnClickListener(view -> {
            userRepository.deleteUser(userId);
            Intent intent = new Intent(AdminProfileView.this, AdminAllProfilesView.class);
            startActivity(intent);
        });

        // Facility profile button listener
        facilityProfileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminProfileView.this, AdminFacilityProfileView.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    /**
     * Loads the user profile data from Firestore and updates the UI accordingly.
     * If the user has a facility profile, the facility profile button is shown.
     */
    private void loadProfileData() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Log.i(TAG, "Clicked on user: " + documentSnapshot.getString("name"));
                            User currentUser = new UserImpl(documentSnapshot.getString("email"),
                                    documentSnapshot.getString("userType"),
                                    documentSnapshot.getString("name"),
                                    Optional.ofNullable(documentSnapshot.getString("phoneNumber")),
                                    documentSnapshot.getBoolean("administratorNotification"),
                                    documentSnapshot.getBoolean("organizerNotification"),
                                    documentSnapshot.getString("profileImage")
                            );
                            nameText.setText(currentUser.getName());
                            emailText.setText(currentUser.getEmail());
                            userTypeText.setText(currentUser.getUserType());
                            phoneNumberText.setText(currentUser.getPhoneNumber().get());
                            loadProfileImage(currentUser.getProfileImage());

                            // Hide facility buttons for non-organizer users or users without a facility profile
                            if (documentSnapshot.getString("userType").equals("PARTICIPANT") ||
                                    documentSnapshot.getString("userType").equals("ADMIN") ||
                                    documentSnapshot.getString("facilityName") == null) {
                                facilityProfileBtn.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error loading user profile", e);
                        }
                    } else {
                        Log.e(TAG, "Error getting current user");
                    }
                });
    }

    /**
     * Loads the profile image from the provided URL and sets it in the ImageView.
     *
     * @param imageUrl the URL of the profile image
     */
    private void loadProfileImage(String imageUrl) {
        Picasso.get().load(imageUrl)
                .into(profileImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Profile image loaded successfully.");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Failed to load profile image", e);
                    }
                });
    }
}
