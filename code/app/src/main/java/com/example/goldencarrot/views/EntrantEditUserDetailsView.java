package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;

import java.util.Optional;

public class EntrantEditUserDetailsView extends AppCompatActivity {
    private static final String TAG = "EditUserDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_edit_user_details);

        EditText nameInput = findViewById(R.id.edit_user_details_name);
        EditText emailInput = findViewById(R.id.edit_user_details_email_input);
        EditText phoneInput = findViewById(R.id.edit_user_details_phone_number);
        Button saveButton = findViewById(R.id.edit_user_details_save_button);

        // Set a click listener on the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String phoneNumber = phoneInput.getText().toString().trim();

                // Create User object
                User user;
                try {
                    // Handle optional phone number using Optional.ofNullable()
                    Optional<String> optionalPhoneNumber =
                            phoneNumber.isEmpty() ? Optional.empty() : Optional.of(phoneNumber);
                    user = new UserImpl(email, UserUtils.PARTICIPANT_TYPE, name, optionalPhoneNumber);

                    // Get the device ID
                    String deviceId = getDeviceId(EntrantEditUserDetailsView.this);

                    // Update user details in Firestore
                    UserRepository userRepository = new UserRepository();
                    userRepository.updateUser(user, deviceId);

                    // Show success message
                    Toast.makeText(EntrantEditUserDetailsView.this,
                            "User details updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EntrantEditUserDetailsView.this,
                            EntrantHomeView.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error creating User object: " + e.getMessage());
                    Toast.makeText(EntrantEditUserDetailsView.this,
                            "Failed to update user details. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Retrieves the Android device ID.
     *
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
