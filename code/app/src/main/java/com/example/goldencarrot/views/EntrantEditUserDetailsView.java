package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        Button backButton = findViewById(R.id.back_button);

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
                    User user = new UserImpl(email, UserUtils.PARTICIPANT_TYPE, name, optionalPhoneNumber);

                    // Get the device ID
                    String deviceId = getDeviceId(EntrantEditUserDetailsView.this);

                    // Update user details in Firestore
                    UserRepository userRepository = new UserRepository();
                    userRepository.updateUser(user, deviceId);

                    // Show success message in dialog
                    ValidationErrorDialog.show(EntrantEditUserDetailsView.this, "Success", "User details updated successfully!");

                    // Navigate to the home view
                    Intent intent = new Intent(EntrantEditUserDetailsView.this, EntrantHomeView.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    ValidationErrorDialog.show(EntrantEditUserDetailsView.this, "Validation Error", e.getMessage());
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantEditUserDetailsView.this, EntrantHomeView.class);
                startActivity(intent);
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
