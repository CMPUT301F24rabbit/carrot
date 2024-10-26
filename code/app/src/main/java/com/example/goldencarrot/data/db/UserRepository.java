package com.example.goldencarrot.data.db;

import android.util.Log;

import com.example.goldencarrot.data.model.user.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Queries User DB
 */
public class UserRepository {
    private static final String TAG = "DB" ;
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Todo Implemet this method
     * @param user
     */
    // Add user to Firestore
    public void addUser(final User user, final String androidId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("userType", user.getUserType());
        userData.put("name", user.getName());
        // Only add phoneNumber if it is present (non-empty)
        user.getPhoneNumber().ifPresent(phone -> userData.put("phoneNumber", phone));

        Log.d(TAG, "Email: " + user.getEmail());
        Log.d(TAG, "User Type: " + user.getUserType());
        Log.d(TAG, "name: " + user.getName());
        user.getPhoneNumber().ifPresent(phone -> Log.d(TAG, "Phone Number: " + phone));

        // Add the user document to the "users" collection using their UID as the document ID
        db.collection("users").document(androidId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added the user
                    System.out.println("User added to Firestore");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    System.err.println("Error adding user to Firestore: " + e.getMessage());
                    Log.e(TAG, "Error adding user to Firestore", e);

                });
    }

    /**
     * Checks if a user exists in Firestore using the Android ID as the document ID,
     * and retrieves the userType if the user exists.
     *
     * @param androidId The Android ID to be used as the document ID.
     * @param callback A callback to handle the result (boolean indicating existence, and userType if exists).
     */
    public void checkUserExistsAndGetUserType(String androidId, UserTypeCallback callback) {
        DocumentReference userRef = db.collection("users").document(androidId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // The document exists, retrieve the userType
                        String userType = documentSnapshot.getString("userType");
                        callback.onResult(true, userType);
                    } else {
                        // The document does not exist
                        callback.onResult(false, null);
                    }
                })
                .addOnFailureListener(e -> {
                    // In case of an error, assume the document does not exist
                    callback.onResult(false, null);
                    Log.e(TAG, "Error checking if user exists: " + e.getMessage(), e);
                });
    }

    /**
     * Updates an existing user in Firestore using the Android ID as the document ID.
     *
     * @param user The User object containing the updated user information.
     * @param androidId The Android ID used as the document ID.
     */
    public void updateUser(final User user, final String androidId) {
        DocumentReference userRef = db.collection("users").document(androidId);

        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("email", user.getEmail());
        updatedUserData.put("userType", user.getUserType());
        updatedUserData.put("name", user.getName());
        // Only add phoneNumber if it is present (non-empty)
        user.getPhoneNumber().ifPresent(phone -> updatedUserData.put("phoneNumber", phone));

        Log.d(TAG, "Updating User: " + androidId);
        Log.d(TAG, "Email: " + user.getEmail());
        Log.d(TAG, "User Type: " + user.getUserType());
        Log.d(TAG, "Name: " + user.getName());
        user.getPhoneNumber().ifPresent(phone -> Log.d(TAG, "Phone Number: " + phone));

        // Update the user document with the new data
        userRef.update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the user
                    Log.d(TAG, "User updated successfully");
                    System.out.println("User updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Log.e(TAG, "Error updating user: " + e.getMessage(), e);
                    System.err.println("Error updating user: " + e.getMessage());
                });
    }

    /**
     * Deletes a user from Firestore using the Android ID as the document ID.
     *
     * @param androidId The Android ID used as the document ID.
     */
    public void deleteUser(final String androidId) {
        // Reference to the user document
        DocumentReference userRef = db.collection("users").document(androidId);

        userRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted the user
                    Log.d(TAG, "User deleted successfully");
                    System.out.println("User deleted successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Log.e(TAG, "Error deleting user: " + e.getMessage(), e);
                    System.err.println("Error deleting user: " + e.getMessage());
                });
    }

    /**
     * Callback interface to handle Firestore query results.
     */
    public interface FirestoreCallback {
        void onSuccess(String userType);
        void onFailure(Exception e);
    }

    /**
     * Callback interface to handle the result of the existence check and userType retrieval.
     */
    public interface UserTypeCallback {
        void onResult(boolean exists, String userType);
    }
}
