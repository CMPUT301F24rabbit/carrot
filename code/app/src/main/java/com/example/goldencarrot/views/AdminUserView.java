package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Displays admin view of user profile
 */
public class AdminUserView extends AppCompatActivity {
    private String userId;
    private FirebaseFirestore db;

    private Button backBtn, deleteBtn;
    private TextView usernameText, emailText, userTypeText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_views_profile);

        db = FirebaseFirestore.getInstance();
        // extract user id
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("currentUserId");
        }
        usernameText = findViewById(R.id.profileUsernameText);
        emailText = findViewById(R.id.profileEmailText);
        userTypeText = findViewById(R.id.profileUserTypeText);
        // use id to find user on firestore
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Log.i(TAG, "clicked on user: " + documentSnapshot.getString("email"));
                            User currentUser = new UserImpl(documentSnapshot.getString("email"),
                                    documentSnapshot.getString("userType"),
                                    documentSnapshot.getString("username"));
                            usernameText.setText(currentUser.getUsername());
                            emailText.setText(currentUser.getEmail());
                            userTypeText.setText(currentUser.getUserType());
                        } catch (Exception e) {

                        }
                    } else {
                        Log.e(TAG,"error getting current user");
                    }
                });
        // displaying user details
        //usernameText.setText(currentUser.getUsername());
        //emailText.setText(currentUser.getEmail());

        //back button
        backBtn = findViewById(R.id.adminViewProfileBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //delete button
        deleteBtn = findViewById(R.id.deleteProfileBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
