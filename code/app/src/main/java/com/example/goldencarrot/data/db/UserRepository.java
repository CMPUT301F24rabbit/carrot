package com.example.goldencarrot.data.db;

import android.util.Log;

import com.example.goldencarrot.data.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
    private FirebaseAuth mAuth;

    private CollectionReference userCollection;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Todo Implemet this method
     * @param user
     */
    // Add user to Firestore
    public void addUser(User user, String uid) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("userType", user.getUserType());
        userData.put("username", user.getUsername());


        Log.d(TAG, "Email: " + user.getEmail());
        Log.d(TAG, "User Type: " + user.getUserType());
        Log.d(TAG, "Username: " + user.getUsername());

        // Add the user document to the "users" collection using their UID as the document ID
        db.collection("users").document(uid)
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
     * Retrieves the current user's type from the Firestore users collection.
     *
     * @param callback a callback to handle the result (userType)
     */
    public void getUserTypeFromFirestore(FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();  // Get the authenticated user's UID

            // Reference to the user's document in the "users" collection
            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve the userType from the Firestore document
                            String userType = documentSnapshot.getString("userType");
                            callback.onSuccess(userType);  // Pass the userType to the callback
                        } else {
                            callback.onFailure(new Exception("User document does not exist"));
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onFailure(e);
                    });

        } else {
            callback.onFailure(new Exception("No authenticated user found"));
        }
    }

    /**
     * Callback interface to handle Firestore query results.
     */
    public interface FirestoreCallback {
        void onSuccess(String userType);
        void onFailure(Exception e);
    }
}
