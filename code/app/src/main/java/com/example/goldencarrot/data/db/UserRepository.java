package com.example.goldencarrot.data.db;

import android.util.Log;

import com.example.goldencarrot.data.model.user.User;
import com.google.firebase.firestore.CollectionReference;
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

    // Add a new user to the database

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
}
