package com.example.goldencarrot.data.db;

import android.util.Log;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code WaitListRepository} class provides methods to create, update, delete,
 * and query waitlist documents in Firestore. It interacts with Firestore to persist
 * waitlist data such as user names and their status.
 */

public class WaitListRepository implements WaitListDb{
    private static final String TAG = "WaitListRepository";
    private final FirebaseFirestore db;
    private final CollectionReference waitListRef;

    /**
     * Constructs a new {@code WaitListRepository} with the Firestore instance.
     */
    public WaitListRepository() {
        db = FirebaseFirestore.getInstance();
        waitListRef = db.collection("waitlists");  // Firestore collection named "waitlists"
    }

    /**
     * Creates a new waitlist document in Firestore.
     *
     * @param waitList the waitlist to be created
     * @param docId    the document ID for this waitlist in Firestore
     */
    @Override
    public void createWaitList(WaitList waitList, String docId) {
        Map<String, Object> waitListData = new HashMap<>();

        waitListData.put("size", waitList.getUserArrayList().size());
        waitListData.put("limit", waitList.getLimitNumber());

        // Add users from the waitlist to Firestore
        for (UserImpl user : waitList.getUserArrayList()) {
            waitListData.put(user.getName(), "waiting");  // Default status to "waiting"
        }

        // Add the waitlist document to Firestore
        waitListRef.document(docId)
                .set(waitListData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "WaitList created successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating waitlist", e));
    }

    @Override
    public void addUserToWaitList(String docId, UserImpl user, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentSize = documentSnapshot.getLong("size");  // Get current size
                        Long limit = documentSnapshot.getLong("limit");  // Get limit

                        if (currentSize != null && limit != null && currentSize < limit) {
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put(user.getName(), "waiting");
                            updateData.put("size", currentSize + 1);  // Increment size

                            // Add the user to the waitlist and update the size
                            waitListRef.document(docId)
                                    .update(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User added to waitlist successfully");
                                        callback.onSuccess(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error adding user to waitlist", e);
                                        callback.onFailure(e);
                                    });
                        } else {
                            Log.d(TAG, "Waitlist is full");
                            callback.onSuccess(false);  // Waitlist is full
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching waitlist", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Updates the status of a user in the waitlist document in Firestore.
     *
     * @param docId   the document ID of the waitlist
     * @param user    the user to update
     * @param status  the new status of the user (e.g., "accepted", "rejected", etc.)
     */

    @Override
    public void updateUserStatusInWaitList(String docId, UserImpl user, String status) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(user.getName(), status);

        /**
         * Todo make a private method to validate the status input
         */

        // Update the user status in the waitlist document
        waitListRef.document(docId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User status updated successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating user status", e));
    }

    /**
     * Deletes a waitlist document from Firestore.
     *
     * @param docId the document ID of the waitlist to delete
     */
    @Override
    public void deleteWaitList(String docId) {
        waitListRef.document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "WaitList deleted successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting waitlist", e));
    }

    /**
     * Checks if a user is in the waitlist.
     *
     * @param docId the document ID of the waitlist
     * @param user  the user to check
     * @param callback a callback that handles the result
     */
    @Override
    public void isUserInWaitList(String docId, UserImpl user, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains(user.getName())) {
                        callback.onSuccess(true); // User found
                    } else {
                        callback.onSuccess(false); // User not found
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error checking if user is in waitlist", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Checks the status of a user in the waitlist.
     *
     * @param docId the document ID of the waitlist
     * @param user  the user to check
     * @param callback a callback that handles the result
     */
    @Override
    public void getUserStatus(String docId, UserImpl user, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains(user.getName())) {
                        String status = documentSnapshot.getString(user.getName());
                        callback.onSuccess(status); // Return user's status
                    } else {
                        callback.onSuccess(null); // User not found or no status
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching user status", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Retrieves a list of users with a specified status in the waitlist document.
     *
     * @param docId  the document ID of the waitlist
     * @param status the status to filter users by (e.g., "waiting", "accepted")
     * @param callback a callback that returns a list of rnames with the specified status
     */
    @Override
    public void getUsersWithStatus(final String docId,
                                   final String status,
                                   final FirestoreCallback callback) {
        /**
         * Make a private method to validate the status input
         */
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> usersWithStatus = new ArrayList<>();

                        // Loop through all users in the document
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                // Skip metadata fields like "size" or "limit"
                                if (!entry.getKey().equals("size") &&
                                        !entry.getKey().equals("limit")) {
                                    // Check if the user has the specified status
                                    if (entry.getValue().toString().equals(status)) {
                                        usersWithStatus.add(entry.getKey());
                                    }
                                }
                            }
                        }

                        callback.onSuccess(usersWithStatus);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching users with status " + status, e);
                    callback.onFailure(e);
                });
    }

    /**
     * A callback interface for Firestore queries.
     */
    public interface FirestoreCallback {
        void onSuccess(Object result);
        void onFailure(Exception e);
    }
    /**
     * Retrieves the list of users who are on the waiting list for the given event.
     *
     * @param docId the document ID of the waitlist
     * @param callback the callback that returns the list of user names
     */
    public void getWaitlistForEvent(String docId, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> waitlistUsers = new ArrayList<>();

                        // Loop through all users in the document
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                // Skip metadata fields like "size" or "limit"
                                if (!entry.getKey().equals("size") && !entry.getKey().equals("limit")) {
                                    // Add the user to the waitlist list
                                    waitlistUsers.add(entry.getKey());
                                }
                            }
                        }

                        // Return the list of users in the waitlist
                        callback.onSuccess(waitlistUsers);
                    } else {
                        // Return an empty list if the waitlist document doesn't exist
                        callback.onSuccess(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching waitlist for event", e);
                    callback.onFailure(e);
                });
    }

}
