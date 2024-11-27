package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Optional;
/**
 * The {@code EntrantEditUserDetailsView} activity allows entrants to edit their profile details,
 * including name, email, phone number, and notification preferences. It interacts with Firestore
 * to retrieve and update the user's data.
 */
public class EntrantEditUserDetailsView extends AppCompatActivity {
    private static final String TAG = "EditUserDetails";
    private static final String PREFS_NAME = "UserPreferences";
    private static final String PREF_ORGANIZER_NOTIFICATIONS = "organizer_notifications";
    private static final String PREF_ADMIN_NOTIFICATIONS = "administer_notifications";
    private String userProfileImage;
    private String originalProfileImage;
    private boolean isOrganizerNotificationsEnabled;
    private boolean isAdminNotificationsEnabled;

    private EditText nameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private Switch switchOrganizerNotifications;
    private Switch switchAdminNotifications;
    private ImageView profileImage; // This has to be updated to whatever is in firebase

    private AlertDialog profilePictureDialogue;
    private ActivityResultLauncher<Intent> openGallery;

    /**
     * Initializes the activity, sets up the UI elements, loads the current user data from Firestore,
     * and handles user interactions such as saving or updating the user details.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_edit_user_details);

        // Validate User and deviceID
        String deviceId = getDeviceId(this);
        UserRepository userRepository = new UserRepository();
        userRepository.getSingleUser(deviceId, new UserRepository.FirestoreCallbackSingleUser(){
            @Override
            public void onSuccess(UserImpl user) {
                loadUserData();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Invalid device ID or user not found, redirecting you to login.");
                redirectToLogin();
            }
        });

        nameInput = findViewById(R.id.edit_user_details_name);
        emailInput = findViewById(R.id.edit_user_details_email_input);
        phoneInput = findViewById(R.id.edit_user_details_phone_number);
        switchOrganizerNotifications = findViewById(R.id.switch_organizer_notifications);
        switchAdminNotifications = findViewById(R.id.switch_admin_notifications);
        profileImage = findViewById(R.id.profile_image);

        Button saveButton = findViewById(R.id.edit_user_details_save_button);
        Button backButton = findViewById(R.id.back_button_notifications);

        // setup the Activity Result Launcher
        setupActivityResultLauncher();

        editProfilePictureListener();

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

        setupSaveButton();

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

    /**
     * Loads the current user's data from Firestore and populates the input fields.
     */
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

                // Set profile picture
                userProfileImage = user.getProfileImage();

                if(userProfileImage == null || userProfileImage.isEmpty()){
                    userProfileImage = getGenericProfilePictureURL(user.getName());
                }

                originalProfileImage = userProfileImage;

                Picasso.get().load(userProfileImage)
                        .error(R.drawable.profilepic1)
                        .into(profileImage);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error loading user data: " + e.getMessage());
            }
        });
    }

    private void editProfilePictureListener() {
        profileImage.setOnLongClickListener(v -> {
            showProfilePicturePopup();
            return true;
        });
    }

    private void setupActivityResultLauncher(){
        openGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Get our image URI
                        Uri selectedImageUri = result.getData().getData();
                        if(selectedImageUri != null) {
                            // CROP IT BIIIIITCH
                            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_profile_image.jpg"));

                            UCrop.Options options = new UCrop.Options();
                            options.setCircleDimmedLayer(true); // This gets us thecircle cropp
                            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                            options.setCompressionQuality(90);

                            UCrop.of(selectedImageUri, destinationUri)
                                            .withAspectRatio(1, 1)
                                            .withMaxResultSize(500, 500)
                                            .withOptions(options)
                                            .start(EntrantEditUserDetailsView.this);

                            Log.d("ProfileImage", "Selected Image URI: " + selectedImageUri.toString());
                        }
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            // UCROP SUCCESS
            Uri croppedImageUri = UCrop.getOutput(data);

            if(croppedImageUri != null) {
                // Set to the Image View and upload to FB
                Log.d("UCROP", "Cropped Image  URI: " + croppedImageUri.toString());
                profileImage.setImageURI(croppedImageUri);
                sendImagetoFB(croppedImageUri);
            } else {
                Log.e("UCROP", "Cropped image URI is null.");
                ValidationErrorDialog.show(this,"Error", "Failed to crop the image. Please try again.");
            }
        } else if(resultCode == UCrop.RESULT_ERROR) {
            //UCROP FAILUREEEEEEE
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("UCROP ERROR", cropError.getMessage());
                ValidationErrorDialog.show(this, "Error", "Image cropping failed: " + cropError.getMessage());
            }
        }
    }

    private void sendImagetoFB (Uri imageUri){
        // Get reference to 'profile' folder
        String userId = getDeviceId(this);
        String storagePath = "profile/userPictures/" + userId + "_profile.png";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);

        // Now upload the image
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // get download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                userProfileImage = uri.toString(); //SAVE IT
                Log.d("ProfileImage", "Image uploaded successfully: " + userProfileImage);
               updateFirestoreImageURL(userProfileImage);
                Toast.makeText(this, "Profile image was sucessfully updated!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.e("Profile Image", "Failed to get download  URL" + e.getMessage());
            });
        }).addOnFailureListener(e -> {
            Log.e("ProfileImage", "Image upload failed" + e.getMessage());
        });
    }

    private void  saveUserDetailsToFS() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phoneNumber = phoneInput.getText().toString().trim();

        try {
            // Validate inputs
            verifyInputs(email, phoneNumber, name);


            // CONTROL Z UP TO  HERE
            // If image is generic, regenerate based on name
            if (!userProfileImage.equals(originalProfileImage) && userProfileImage.contains("/profile/generic/")) {
                // Regenerate based on new name
                fetchDefaultProfilePictureUrl(name, defaultProfileUrl -> {
                    userProfileImage = defaultProfileUrl;
                    // Save
                    saveUserToFirestoreHelper(name, email, phoneNumber);
                });
            } else if (!originalProfileImage.equals(getGenericProfilePictureURL(name))) {
                // NAME HAS CHANGED
                fetchDefaultProfilePictureUrl(name, defaultProfileUrl ->{
                    userProfileImage = defaultProfileUrl;
                    saveUserToFirestoreHelper(name, email, phoneNumber);
                });
            } else {
                // No change
                saveUserToFirestoreHelper(name, email, phoneNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            if (!isFinishing()) {
                ValidationErrorDialog.show(this, "Validation Error", e.getMessage());
            }
        }
    }

    private void saveUserToFirestoreHelper(String name, String email, String phoneNumber) {
        try {
            Optional<String> optionalPhoneNumber = phoneNumber.isEmpty() ? Optional.empty() : Optional.of(phoneNumber);
            User user = new UserImpl(
                    email,
                    UserUtils.PARTICIPANT_TYPE,
                    name,
                    optionalPhoneNumber,
                    isAdminNotificationsEnabled,
                    isOrganizerNotificationsEnabled,
                    userProfileImage
            );

            // Get device ID
            String deviceId = getDeviceId(this);

            // Update details into Firestore
            UserRepository userRepository = new UserRepository();
            userRepository.updateUser(user, deviceId);

            // Show success msg
            ValidationErrorDialog.show(this, "Success", "User details updated successfully!");

            // Navigate to home view
            Intent intent = new Intent(EntrantEditUserDetailsView.this, EntrantHomeView.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error while saving user details: " + e.getMessage());
            ValidationErrorDialog.show(this, "Error", e.getMessage());
        }
    }

    private String getGenericProfilePictureURL(String name) {
        // Ensure name isn't empty/null
        if (TextUtils.isEmpty(name)) {
            Log.e(TAG, "Name cannot be empty for assigning a profile picture.");
            return "https://firebasestorage.googleapis.com/v0/b/goldencarrotdatabase.appspot.com/o/profile%2Fgeneric%2Fdefault.png?alt=media";
        }

        char firstLetter = Character.toLowerCase(name.charAt(0)); // Convert to lowercase
        return "https://firebasestorage.googleapis.com/v0/b/goldencarrotdatabase.appspot.com/o/profile%2Fgeneric%2F"
                + firstLetter + ".png?alt=media";
    }

    private interface OnProfilePictureFetched {
        void onSuccess(String url);
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.edit_user_details_save_button);
        saveButton.setOnClickListener(v -> saveUserDetailsToFS());
    }

    private void updateFirestoreImageURL(String imageURL) {
        if(TextUtils.isEmpty(imageURL)){
            Log.e(TAG,  "Cannot update Firestore withan empty image URL.");
            return;
        }

        String userId = getDeviceId(this);
        UserRepository userRepository =  new UserRepository();

        // Get user and update their profile image
        userRepository.getSingleUser(userId, new UserRepository.FirestoreCallbackSingleUser() {
            @Override
            public void onSuccess(UserImpl user) {
                user.setProfileImage(imageURL);

                // Save it to firestore
                userRepository.updateUser(user, userId);
                Log.d("Firestore", "User profile image upated successfully in Firestore");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Failed to fetch user for updating image URL: " + e.getMessage());
            }
        });
    }

    /**
     * Validates the user inputs (name, email, phone number).
     *
     * @param email The user's email.
     * @param phoneNumber The user's phone number.
     * @param name The user's name.
     * @throws Exception If any validation fails (e.g., invalid email format, missing name).
     */
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

    /**
     * Retrieves the unique device ID for the current device.
     *
     * @param context The context of the application.
     * @return The unique device ID.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void showProfilePicturePopup() {
        // Alert Dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture Options");

        // Options:
        String[] options = {"Remove Picture", "Upload Picture"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Remove the picture
                resetToGeneric();
                loadImageWithFallback(userProfileImage, profileImage);
                Log.d(TAG, "Profile Picture removed and reverted to generic.");
            } else if (which == 1){
                // Make intent to open the image gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                // Now launch intent
                openGallery.launch(intent);
            }
        });

        // Show Dialog
        profilePictureDialogue = builder.create();
        profilePictureDialogue.show();

    }

    @Override
    protected void onDestroy() {
        if(profilePictureDialogue != null && profilePictureDialogue.isShowing()) {
            profilePictureDialogue.dismiss();
        }
        super.onDestroy();
    }

    private void resetToGeneric() {
        String name = nameInput.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Log.e(TAG, "Name cannot be empty to reset profile picture.");
            loadImageWithFallback(originalProfileImage, profileImage);
            return;
        }

        fetchDefaultProfilePictureUrl(name, defaultProfileUrl -> {
            userProfileImage = defaultProfileUrl;

            // Show generic temporarily in UI
            loadImageWithFallback(userProfileImage, profileImage);

            Toast.makeText(this, "Profile picture reset to default. Save changes to apply.", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchDefaultProfilePictureUrl(String name, OnProfilePictureFetched callback) {
        if (TextUtils.isEmpty(name)) {
            Log.e(TAG, "Name cannot be empty for assigning a profile picture.");
            callback.onSuccess(getGenericProfilePictureURL(String.valueOf('x'))); // Default fallback
            return;
        }

        char firstLetter = Character.toLowerCase(name.charAt(0)); // Convert to lowercase
        String filePath = "profile/generic/" + firstLetter + ".png";

        Log.d(TAG, "Attempting to fetch profile picture from: " + filePath);

        // Firebase Storage reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(filePath);

        // Fetch the download URL
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "Successfully fetched profile picture URL: " + uri.toString());
            callback.onSuccess(uri.toString());
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to fetch default profile picture URL", e);
            callback.onSuccess("android.resource://" + getPackageName() + "/drawable/profilepic1"); // Fallback
        });
    }

    private void loadImageWithFallback(String imageUrl, ImageView imageView){
        Picasso.get()
                .load(imageUrl)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Image loaded successfully: " + imageUrl);

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Failed to load image: " + imageUrl + ", reverting to original");

                        // Go back to letter
                        String name = nameInput.getText().toString().trim();
                        String fallbackImageUrl = getGenericProfilePictureURL(nameInput.getText().toString().trim());
                        Picasso.get().load(fallbackImageUrl).into(imageView);
                    }
                });
    }

    private void redirectToLogin(){
        Intent intent = new Intent(this, EntrantHomeView.class);
        startActivity(intent);
        finish();
    }
}
