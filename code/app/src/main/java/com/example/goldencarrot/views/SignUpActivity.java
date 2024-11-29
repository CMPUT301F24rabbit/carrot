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

public class SignUpActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String userType;

    private UserRepository userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_sign_up);

        userDb = new UserRepository();

        // Default user type is "Participant"
        userType = UserUtils.PARTICIPANT_TYPE;

        findViewById(R.id.sign_up_create_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.sign_up_email_input);
                EditText phoneNumber = findViewById(R.id.sign_up_phone_number);
                EditText name = findViewById(R.id.sign_up_name);
                Boolean nAdmin = true;
                Boolean nOrg = true;

                String deviceId = Settings.Secure.getString(
                        SignUpActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                try {
                    verifyInputs(
                            email.getText().toString(),
                            phoneNumber.getText().toString(),
                            name.getText().toString()
                    );

                    String defaultProfilePic = "android.resource://" + getPackageName() + "/drawable/profilepic1";

                    addUserToFirestore(deviceId, name.getText().toString(), email.getText().toString(), Optional.of(phoneNumber.getText().toString()), nAdmin, nOrg, defaultProfilePic);

                } catch (Exception e) {
                    ValidationErrorDialog.show(SignUpActivity.this, "Validation Error", e.getMessage());
                }
            }
        });
    }

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
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

    private interface OnLocationFetchedListener {
        void onLocationFetched(Location location);
    }
}
