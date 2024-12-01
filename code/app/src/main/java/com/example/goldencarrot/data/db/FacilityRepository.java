package com.example.goldencarrot.data.db;

import android.util.Log;
import androidx.annotation.Nullable;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FacilityRepository {
    private static final String TAG = "FacilityRepository";
    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    /**
     * Constructor initializes the Firestore instance and references the "users" collection.
     */
    public FacilityRepository() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    /**
     * Adds facility-related fields to a user document in Firestore.
     *
     * @param userId The ID of the user document to update.
     * @param facilityName The name of the facility.
     * @param location The location of the facility.
     * @param imageURL The URL of the facility's image.
     */
    public void addFacilityFields(String userId, String facilityName, String location, String imageURL) {
        // Create a map for the new fields
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityName", facilityName);
        facilityData.put("location", location);
        facilityData.put("imageURL", imageURL);

        // Update the specific user document with the new fields
        db.collection("users").document(userId)
                .update(facilityData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility fields added successfully for user: " + userId))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding facility fields", e));
    }

    /**
     * Fetches facility-related fields from a user document.
     *
     * @param userId The ID of the user document to fetch.
     * @param callback A callback to handle the retrieved data or error.
     */
    public void getFacilityFields(String userId, FacilityCallback callback) {
        usersCollection.document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String facilityName = documentSnapshot.getString("facilityName");
                        String location = documentSnapshot.getString("location");
                        String imageURL = documentSnapshot.getString("imageURL");

                        callback.onSuccess(facilityName, location, imageURL);
                    } else {
                        Log.w(TAG, "No user found with ID: " + userId);
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching facility fields", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Callback interface for facility-related Firestore operations.
     */
    public interface FacilityCallback {
        void onSuccess(String facilityName, String location, String imageURL);
        void onFailure(Exception e);
    }
}