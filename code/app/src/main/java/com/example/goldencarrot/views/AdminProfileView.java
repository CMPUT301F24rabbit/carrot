package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

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
 * Displays admin view of user profile
 */
public class AdminProfileView extends AppCompatActivity {
    private String userId;
    private FirebaseFirestore db;
    private ImageView profileImageView;
    private Button backBtn, deleteBtn, facilityProfileBtn;
    private TextView nameText, emailText, userTypeText, phoneNumberText;
    private UserRepository userRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_views_profile);

        db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository();
        // extract user id
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

        // use id to find user on firestore
        loadProfileData();

        // back button
        backBtn = findViewById(R.id.adminViewProfileBackBtn);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminProfileView.this, AdminAllProfilesView.class);
            startActivity(intent);
        });

        //delete button
        deleteBtn = findViewById(R.id.deleteProfileBtn);
        deleteBtn.setOnClickListener(view -> {
            userRepository.deleteUser(userId);
            Intent intent = new Intent(AdminProfileView.this, AdminAllProfilesView.class);
            startActivity(intent);
        });
        // facility profile button
        facilityProfileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminProfileView.this, AdminFacilityProfileView.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void loadProfileData() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Log.i(TAG, "clicked on user: " + documentSnapshot.getString("name"));
                            User currentUser = new UserImpl(documentSnapshot.getString("email"),
                                    documentSnapshot.getString("userType"),
                                    documentSnapshot.getString("name"), Optional.ofNullable(documentSnapshot.getString("phoneNumber")),
                                    documentSnapshot.getBoolean("administratorNotification"),
                                    documentSnapshot.getBoolean("organizerNotification"),
                                    documentSnapshot.getString("profileImage")
                            );
                            nameText.setText(currentUser.getName());
                            emailText.setText(currentUser.getEmail());
                            userTypeText.setText(currentUser.getUserType());
                            phoneNumberText.setText(currentUser.getPhoneNumber().get());
                            loadProfileImage(currentUser.getProfileImage());

                            // hide facility buttons if not organizer, or organizer with no facility profile
                            if (documentSnapshot.getString("userType").equals("PARTICIPANT") ||
                                    documentSnapshot.getString("userType").equals("ADMIN") ||
                                    documentSnapshot.getString("facilityName") == null) {
                                facilityProfileBtn.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {

                        }
                    } else {
                        Log.e(TAG,"error getting current user");
                    }
                });
    }
    private void loadProfileImage(String imageUrl){
        Picasso.get().load(imageUrl)
                .into(profileImageView, new com.squareup.picasso.Callback(){
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
