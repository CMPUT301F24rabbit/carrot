package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;

import java.util.Optional;

public class EntrantEditUserDetailsView extends AppCompatActivity {
    private static final String TAG = "EditUserDetails";
    private static final String PREFS_NAME = "UserPreferences";
    private static final String PREF_ORGANIZER_NOTIFICATIONS = "organizer_notifications";
    private static final String PREF_ADMIN_NOTIFICATIONS = "administer_notifications";
    private boolean isOrganizerNotificationsEnabled;
    private boolean isAdminNotificationsEnabled;


    private EditText nameInput;
    private EditText emailInput;
    private EditText  phoneInput;
    private Switch switchOrganizerNotifications;
    private Switch switchAdminNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_edit_user_details);

        nameInput = findViewById(R.id.edit_user_details_name);
        emailInput = findViewById(R.id.edit_user_details_email_input);
        phoneInput = findViewById(R.id.edit_user_details_phone_number);
        switchOrganizerNotifications = findViewById(R.id.switch_organizer_notifications);
        switchAdminNotifications = findViewById(R.id.switch_admin_notifications);

        Button saveButton = findViewById(R.id.edit_user_details_save_button);
        Button backButton = findViewById(R.id.back_button_notifications);

        loadUserData();

        isAdminNotificationsEnabled = false;
        isOrganizerNotificationsEnabled = false;

        switchAdminNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isAdminNotificationsEnabled = true;
                    isOrganizerNotificationsEnabled = true;
                } else {
                    isAdminNotificationsEnabled = false;
                    isOrganizerNotificationsEnabled = false;
                }
            }
        });

        // Set a click listener on the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String phoneNumber = phoneInput.getText().toString().trim();

                try {
                    // Validate inputs
                    verifyInputs(email, phoneNumber, name);

                    Optional<String> optionalPhoneNumber = phoneNumber.isEmpty() ? Optional.empty() : Optional.of(phoneNumber);
                    User user = new UserImpl(email, UserUtils.PARTICIPANT_TYPE, name, optionalPhoneNumber, isAdminNotificationsEnabled, isOrganizerNotificationsEnabled);

                    // Get the device ID
                    String deviceId = getDeviceId(EntrantEditUserDetailsView.this);

                    // Update user details in Firestore
                    UserRepository userRepository = new UserRepository();
                    userRepository.updateUser(user, deviceId);

                    // After updating the Firestore data, fetch the updated user details and update the UI
                    userRepository.getSingleUser(deviceId, new UserRepository.FirestoreCallbackSingleUser() {
                        @Override
                        public void onSuccess(UserImpl updatedUser) {
                            // Update the UI with the new user data
                            nameInput.setText(updatedUser.getName());
                            emailInput.setText(updatedUser.getEmail());
                            if (updatedUser.getPhoneNumber().isPresent()) {
                                phoneInput.setText(updatedUser.getPhoneNumber().get());
                            } else {
                                phoneInput.setText(""); // If no phone number is available
                            }

                            switchAdminNotifications.setChecked(updatedUser.getAdminNotification());
                            switchOrganizerNotifications.setChecked(updatedUser.getOrganizerNotifications());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Error fetching updated user details: " + e.getMessage());
                        }
                    });

                    // Show success message in dialog
                    ValidationErrorDialog.show(EntrantEditUserDetailsView.this, "Success", "User details updated successfully!");

                    // Navigate to the home view (or any other view as required)
                    Intent intent = new Intent(EntrantEditUserDetailsView.this, EntrantHomeView.class);
                    startActivity(intent);
                    finish();  // Finish this activity to ensure it doesn't stay in the background
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    ValidationErrorDialog.show(EntrantEditUserDetailsView.this, "Validation Error", e.getMessage());
                }
            }
        });

        // Set click listener for the back button to return to the home view
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantEditUserDetailsView.this, EntrantHomeView.class);
                startActivity(intent);
                finish(); // Finish this activity to avoid navigating back to it
            }
        });
    }

    private void loadUserData() {
        String deviceId = getDeviceId(this);
        UserRepository userRepository = new UserRepository();

        userRepository.getSingleUser(deviceId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                // Populate fields with user data
                nameInput.setText(user.getName());
                emailInput.setText(user.getEmail());
                phoneInput.setText(user.getPhoneNumber().orElse(""));
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error loading user data: " + e.getMessage());
            }
        });

    }


    private void verifyInputs(final String email, final String phoneNumber, final String name) throws Exception {
        if (name.isEmpty()) {
            throw new Exception("Name cannot be empty");
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new Exception("Invalid email format");
        }

        if (!phoneNumber.isEmpty() && !phoneNumber.matches("\\d{10}")) {
            throw new Exception("Phone number must contain exactly 10 digits");
        }
    }

    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
