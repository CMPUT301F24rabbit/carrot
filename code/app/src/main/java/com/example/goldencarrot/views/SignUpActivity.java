package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.authentication.AccountService;
import com.example.goldencarrot.authentication.AccountServiceImpl;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Optional;
/**
 * This activity handles the user sign-up process. It allows the user to input their details (email, phone number, and name),
 * verify the input values, and create an account in Firebase. The user type is set to "Participant" by default, but this can
 * be adjusted in other parts of the application. On successful sign-up, the user is directed to the Entrant home view.
 */
public class SignUpActivity extends AppCompatActivity {

    // Firebase Authentication instance for user authentication
    private FirebaseAuth mAuth;

    // Service for account-related operations
    private AccountService accountService;

    // The type of the user (Participant or Organizer)
    private String userType;

    // Repository for managing user data in Firestore
    private UserRepository userDb;

    /**
     * Called when the activity is created. Sets up the UI elements, and the button listeners for the sign-up process.
     * Initializes necessary services and handles input verification and user account creation.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_sign_up);

        mAuth = FirebaseAuth.getInstance();
        accountService = new AccountServiceImpl(SignUpActivity.this);
        userDb = new UserRepository();

        // Default user type is "Participant"
        userType = UserUtils.PARTICIPANT_TYPE;

        // Set click listener for the sign-up button
        findViewById(R.id.sign_up_create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.sign_up_email_input);
                EditText phoneNumber = findViewById(R.id.sign_up_phone_number);
                EditText name = findViewById(R.id.sign_up_name);
                Boolean nAdmin = true; // Admin status placeholder
                Boolean nOrg = true;   // Organizer status placeholder

                // Get device ID
                String deviceId = Settings.Secure.getString(
                        SignUpActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                try {
                    // Verify user inputs before proceeding
                    verifyInputs(
                            email.getText().toString(),
                            phoneNumber.getText().toString(),
                            name.getText().toString()
                    );

                    // Add user to Firestore
                    addUserToFirestore(deviceId, name.getText().toString(), email.getText().toString(), Optional.of(phoneNumber.getText().toString()), nAdmin, nOrg);

                    // Proceed to the Entrant home view after sign-up
                    Intent intent = new Intent(SignUpActivity.this, EntrantHomeView.class);
                    startActivity(intent);

                } catch (Exception e) {
                    // Show error message in case of invalid input
                    ValidationErrorDialog.show(SignUpActivity.this, "Validation Error", e.getMessage());
                }
            }
        });
    }

    /**
     * Adds the new user to Firestore after validation.
     *
     * @param deviceId The unique device ID of the user's phone.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param phoneNumber The phone number of the user (optional).
     * @param nAdmin The admin status of the user.
     * @param nOrg The organizer status of the user.
     */
    private void addUserToFirestore(String deviceId, String name, String email, Optional<String> phoneNumber, Boolean nAdmin, Boolean nOrg) {
        try {
            // Create a new user object
            User newUser = new UserImpl(email, userType, name, phoneNumber, nAdmin, nOrg);
            // Add the user to Firestore database
            userDb.addUser(newUser, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            // Show validation error dialog if adding user fails
            ValidationErrorDialog.show(SignUpActivity.this, "Error", "Invalid user type");
        }
    }

    /**
     * Verifies the user input (email, phone number, and name) before proceeding with the sign-up process.
     *
     * @param email The user's email address.
     * @param phoneNumber The user's phone number.
     * @param name The user's name.
     * @throws Exception If any of the input fields are invalid.
     */
    private void verifyInputs(final String email, final String phoneNumber, final String name) throws Exception {
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            throw new Exception("Invalid email format");
        }

        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("\\d{10}")) {
            throw new Exception("Phone number must contain exactly 10 digits");
        }

        if (TextUtils.isEmpty(name)) {
            throw new Exception("Name cannot be empty");
        }
    }

    /**
     * Validates the email format using Android's built-in pattern matcher.
     *
     * @param email The email address to validate.
     * @return true if the email format is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
