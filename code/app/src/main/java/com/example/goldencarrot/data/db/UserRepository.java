package com.example.goldencarrot.data.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides necessary methods to update, delete, and write User model objects into
 * firebase User table. The User Id will be the same as the device ID
 *
 * Please see firebase for more detatils on the document structure
 */
public class UserRepository {
    private static final String TAG = "DB" ;
    private FirebaseFirestore db;
    private CollectionReference userCollection;
    private List<DocumentSnapshot> listOfUsers;

    public UserRepository(FirebaseFirestore firestore) {
        this.db = firestore;
    }

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a new User to firebase users table.
     * Provides on success and on failure listenings to handle the result of the operation.
     * @param user model object to add.
     */
    public void addUser(final User user, final String androidId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("userType", user.getUserType());
        userData.put("name", user.getName());
        // Only add phoneNumber if it is present (non-empty)
        user.getPhoneNumber().ifPresent(phone -> userData.put("phoneNumber", phone));
        userData.put("organizerNotification", user.getOrganizerNotifications());
        userData.put("adminNotification", user.getAdminNotification());
        userData.put("profileImage", user.getProfileImage());

        Log.d(TAG, "Email: " + user.getEmail());
        Log.d(TAG, "User Type: " + user.getUserType());
        Log.d(TAG, "name: " + user.getName());
        Log.d(TAG, "Profile Image: " + user.getProfileImage());
        user.getPhoneNumber().ifPresent(phone -> Log.d(TAG, "Phone Number: " + phone));

        // Add the user document to the "users" collection using their UID as the document ID
        db.collection("users").document(androidId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added the user
                    // set user's firebase id
                    user.setUserId(androidId);
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
     * @param callback A callback to handle the result of the operation.
     */
    public void checkUserExistsAndGetUserType(String androidId, UserTypeCallback callback) {
        DocumentReference userRef = db.collection("users").document(androidId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // The document exists, get the userType
                        String userType = documentSnapshot.getString("userType");
                        callback.onResult(true, userType);
                    } else {
                        // The document does not exist
                        callback.onResult(false, null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Fatal Error
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
        updatedUserData.put("adminNotification", user.getAdminNotification());
        updatedUserData.put("organizerNotification", user.getOrganizerNotifications());
        // Add phoneNumber if it is not null
        user.getPhoneNumber().ifPresent(phone -> updatedUserData.put("phoneNumber", phone));
        updatedUserData.put("profileImage", user.getProfileImage());

        Log.d(TAG, "Updating User: " + androidId);
        Log.d(TAG, "Email: " + user.getEmail());
        Log.d(TAG, "User Type: " + user.getUserType());
        Log.d(TAG, "Name: " + user.getName());
        Log.d(TAG, "Profile Image: " + user.getProfileImage());
        user.getPhoneNumber().ifPresent(phone -> Log.d(TAG, "Phone Number: " + phone));

        // Update the user document with the new data
        userRef.update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the user
                    Log.d(TAG, "User updated successfully");
                    System.out.println("User updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e(TAG, "Error updating user: " + e.getMessage(), e);
                    System.err.println("Error updating user: " + e.getMessage());
                });
    }

    /**
     * Deletes a user from Firestore using the Android ID as the document ID.
     *
     * @param androidId The Android device ID used as the document ID to delete
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
     * Retrieves all users from the Firestore user collection
     * @param callback handles the result of the query
     */
    public void getAllUsersFromFirestore(FirestoreCallbackAllUsers callback) {
        userCollection = db.collection("users");
        // get all documents, code from user TomH, downloaded 24/10/24:
        // https://stackoverflow.com/questions/50727254/how-to-retrieve-all-documents-from-a-collection-in-firestore
        userCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listOfUsers = task.getResult().getDocuments();
                            callback.onSuccess(listOfUsers);
                        } else {
                            callback.onFailure(new Exception("No users found"));
                        }
                    }
                });
    }

    /**
     * Queries a user from Firestore by their Android ID.
     *
     * @param androidId the device Id of the user, assumed to be unique
     * @param callback handles the result of the query
     */
    public void getSingleUser(String androidId, FirestoreCallbackSingleUser callback) {
        DocumentReference userRef = db.collection("users").document(androidId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            UserImpl user = new UserImpl(documentSnapshot.getString("email"),
                                    documentSnapshot.getString("userType"),
                                    documentSnapshot.getString("name"),
                                    Optional.ofNullable(documentSnapshot.getString("phoneNumber")),
                                    documentSnapshot.getBoolean("adminNotification"),
                                    documentSnapshot.getBoolean("organizerNotification"),
                                    documentSnapshot.getString("profileImage")
                            );
                            callback.onSuccess(user); // Pass the user object to the callback
                        } catch (Exception e) {

                        }
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    Log.e(TAG, "Error fetching user: " + e.getMessage(), e);
                });
    }

    /**
     * Callback interface for querying for a single user.
     */
    public interface FirestoreCallbackSingleUser {
        void onSuccess(UserImpl user);
        void onFailure(Exception e);
    }

    /**
     * Callback interface to handle Firestore query results for users collection
     */
    public interface FirestoreCallbackAllUsers {
        void onSuccess(List<DocumentSnapshot> listOfUsers);
        void onFailure(Exception e);
    }
    /**
     * Callback interface to handle the result of the existence check and userType retrieval.
     */
    public interface UserTypeCallback {
        void onResult(boolean exists, String userType);
    }
}
