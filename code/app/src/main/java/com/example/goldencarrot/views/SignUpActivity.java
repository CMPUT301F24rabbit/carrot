package com.example.goldencarrot.views;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This activity handles the user sign-up process. It allows the user to input their details (email, phone number, and name),
 * verify the input values, and create an account in Firebase. The user type is set to "Participant" by default, but this can
 * be adjusted in other parts of the application. On successful sign-up, the user is directed to the Entrant home view.
 */

public class SignUpActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String userType;

    // Repository for managing user data in Firestore

    private UserRepository userDb;
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
    registerForActivityResult(new ActivityResultContracts
        .RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
            if (fineLocationGranted != null && fineLocationGranted) {
                Log.i(TAG, "location exact on");
                // Precise location access granted.
            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                // Only approximate location access granted.
                Log.i(TAG, "location approximate on");
            } else {
                // No location access granted.
                Log.i(TAG, "location not granted");
            }
        }
    );
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

        userDb = new UserRepository();

        // Default user type is "Participant"
        userType = UserUtils.PARTICIPANT_TYPE;

        // request geolocation permissions
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });

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

                    // Fetch default user profile URL from firbecase storage
                    String imageName = name.getText().toString();
                    fetchDefaultProfilePictureUrl(imageName, defaultProfileUrl ->{
                        // Add user to Firestore
                        Log.d(TAG, "Default profile picture URL fetched: " + defaultProfileUrl);
                        addUserToFirestore(deviceId, name.getText().toString(), email.getText().toString(), Optional.of(phoneNumber.getText().toString()), nAdmin, nOrg, defaultProfileUrl);
                        // Add user to Firestore

                        // Proceed to the Entrant home view after sign-up
                        Intent intent = new Intent(SignUpActivity.this, EntrantHomeView.class);
                        startActivity(intent);
                    });

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
     * @param defaultProfileUrl The default profile users get assigned
     */
    private void addUserToFirestore(String deviceId, String name, String email, Optional<String> phoneNumber, Boolean nAdmin, Boolean nOrg, String defaultProfileUrl) {
        try {
            getLocation(location -> {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Map<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("userType", userType);
                userData.put("name", name);
                phoneNumber.ifPresent(phone -> userData.put("phoneNumber", phone));
                userData.put("organizerNotification", nOrg);
                userData.put("adminNotification", nAdmin);
                userData.put("profileImage", defaultProfileUrl);
                userData.put("latitude", latitude);
                userData.put("longitude", longitude);

                userDb.addUserWithLocation(userData, deviceId, () -> {
                    Log.d(TAG, "User added to Firestore with location: " + userData);
                    Intent intent = new Intent(SignUpActivity.this, EntrantHomeView.class);
                    startActivity(intent);
                });

            });
        } catch (Exception e) {
            Log.e(TAG, "Error adding user to Firestore: " + e.getMessage());
            ValidationErrorDialog.show(SignUpActivity.this, "Error", "Failed to add user.");
        }
    }

    private void getLocation(OnLocationFetchedListener listener) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.i(TAG, "location not granted");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            listener.onLocationFetched(location);
        } else {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    listener.onLocationFetched(location);
                }
            }, null);
        }
    }

    /**
     * Validates the email format using Android's built-in pattern matcher.
     *
     * @param email The email address to validate.
     * @return true if the email format is valid, false otherwise.
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

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void fetchDefaultProfilePictureUrl(String name, OnProfilePictureFetched callback) {
        // Ensure name isn't empty/null
        if(TextUtils.isEmpty(name)){
            Log.e(TAG, "Name cannot be empty for assigning a profile pciture.");
            callback.onSuccess(getGenericProfilePictureURL('x'));
            return;
        }

        // Now get the first letter of users name
        char firstLetter = Character.toLowerCase(name.charAt(0)); // Convert to lowercase and grab the first letter
        String filePath = "profile/generic/" + firstLetter + ".png";
        Log.d(TAG, "Attempting to fetch profile picture from: " + filePath);

        // Now reference the file in Firebase
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(filePath);

        // Getting the download URL
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Pass URL to callback
            Log.d(TAG, "Successfully fetched profile picture URL: " + uri.toString());
            callback.onSuccess(uri.toString());
        }).addOnFailureListener(e -> {
            // Failure
            Log.e(TAG, "Failed to fetch default profile picture URL", e);
            callback.onSuccess("android.resource://" + getPackageName() + "/drawable/profilepic1");
        });
    }

    private String getGenericProfilePictureURL(char firstLetter) {
        return "https://firebasestorage.googleapis.com/v0/b/goldencarrotdatabase.appspot.com/o/profile%2Fgeneric%2F"
                + firstLetter
                + ".png?alt=media";
    }

    private interface OnProfilePictureFetched {
        void onSuccess(String url);
    }
    private interface OnLocationFetchedListener {
        void onLocationFetched(Location location);
    }
}