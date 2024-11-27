package com.example.goldencarrot.data.db;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.util.Log;
import android.widget.Toast;

import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.example.goldencarrot.data.model.waitlist.WaitListConfigurator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
public class WaitListRepository implements WaitListDb {
    private static final String TAG = "WaitListRepository";
    private final FirebaseFirestore db;
    private final CollectionReference waitListRef;

    /**
     * Constructs a new {@code WaitListRepository} with the Firestore instance.
     */
    public WaitListRepository() {
        db = FirebaseFirestore.getInstance();
        waitListRef = db.collection("waitlist");
    }

    /**
     * Creates a new waitlist document in Firestore.
     *
     * @param waitList the waitlist to be created
     */
    @Override
    public void createWaitList(WaitList waitList, String eventName) {
        Log.d("WaitListRepository", "creating waitlist");
        Map<String, Object> waitListData = new HashMap<>();

        // Create a "users" sub-map to store user statuses
        Map<String, String> usersMap = new HashMap<>();

        waitListData.put("eventName", eventName);
        waitListData.put("limit", waitList.getLimitNumber());
        waitListData.put("size", waitList.getUserMap().size());
        waitListData.put("eventId", waitList.getEventId());
        Log.d("WaitListRepository", "creating waitlist for accepted, declined, waiting");

        waitListData.put("users", usersMap);  // Add users map to the main document

        // Add the waitlist document to Firestore
        waitListRef.add(waitListData)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId(); // Get the auto-generated ID
                    Log.d(TAG, "WaitList created successfully with ID: " + generatedId);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error creating waitlist", e));
    }

    public void updateWaitListInDatabase(WaitListConfigurator waitList) {
        // Prepare the data to update
        Map<String, Object> waitListData = new HashMap<>();
        waitListData.put("eventName", waitList.getEventName());
        waitListData.put("limit", waitList.getLimitNumber());
        waitListData.put("size", waitList.getUserMap().size());
        waitListData.put("eventId", waitList.getEventId());

        // Update only the "users" field
        Map<String, String> users = new HashMap<>(waitList.getUserMap());
        waitListData.put("users", users);

        // Perform the update
        waitListRef.document(waitList.getWaitListId())
                .update(waitListData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "WaitList updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating WaitList", e));
    }

    @Override
    public void addUserToWaitList(String docId, User user, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long currentSize = documentSnapshot.getLong("size");
                        Long limit = documentSnapshot.getLong("limit");

                        if (currentSize != null && limit != null && currentSize < limit) {
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("size", currentSize + 1);

                            // Check if the "users" field exists
                            if (!documentSnapshot.contains("users")) {
                                // Initialize the "users" map if it doesn't exist
                                Map<String, String> usersMap = new HashMap<>();
                                usersMap.put(user.getUserId(), UserUtils.WAITING_STATUS);
                                updateData.put("users", usersMap);  // Add the new user in the new map
                            } else {
                                // Add the user to the existing "users" map
                                updateData.put("users." + user.getUserId(), UserUtils.WAITING_STATUS);
                            }

                            // Update Firestore document
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
                            callback.onSuccess(false);
                            Toast.makeText(getApplicationContext(), "The waitlist is full.", Toast.LENGTH_SHORT).show();

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
        updateData.put("users." + user.getUserId(), status);  // Update the status in the users map

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
     * Returns Waitlist object with the same eventId if found
     * @param eventId  The ID of the event for which the waitlist is queried.
     * @param callback A callback to handle the result (a WaitList object or an error).
     */
    @Override
    public void getWaitListByEventId(String eventId, WaitListCallback callback) {
        waitListRef.whereEqualTo("eventId", eventId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Retrieve the users map and populate userArrayList with the user IDs
                        ArrayList<UserImpl> userArrayList = new ArrayList<>();
                        Map<String, String> usersMap = (Map<String, String>) document.get("users");

                        WaitList waitList = new WaitList(
                                // Waitlist limit
                                document.getLong("limit").intValue(),
                                // Waitlist document Id
                                document.getId(),
                                // Event Id
                                eventId,
                                // Event Name
                                document.getString("eventName"),
                                // User Array List
                                usersMap
                        );

                        Log.d(TAG, "Waitlist found for eventId: " + eventId);
                        callback.onSuccess(waitList);
                    } else {
                        Log.w(TAG, "No waitlist found for eventId: " + eventId);
                        callback.onFailure(new Exception("No waitlist document found with the provided eventId."));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to retrieve waitlist for eventId: " + eventId, e);
                    callback.onFailure(e);
                });
    }

    /**
     * Checks if user is in waitlist and returns true or false
     * @param docId   the document ID of the waitlist
     * @param user    the user to check
     * @param callback a callback that handles the result
     */
    @Override
    public void isUserInWaitList(String docId, User user, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("users." + user.getUserId())) {
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
     * Returns an array of userId with the specified waitlist status ("waiting", "accepted", "declined", etc...)
     * @param docId    the document ID of the waitlist
     * @param status   the status to filter users by (e.g., "waiting", "accepted")
     * @param callback a callback that returns a list of names with the specified status
     */
    @Override
    public void getUsersWithStatus(final String docId, final String status, final FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> usersWithStatus = new ArrayList<>();
                        Map<String, Object> usersMap = (Map<String, Object>) documentSnapshot.get("users");
                        for (Map.Entry<String, Object> entry : usersMap.entrySet()) {
                            String currentUserId = entry.getKey();
                            String currentStatus = entry.getValue().toString();
                            if (currentStatus.equals(status)) {
                                usersWithStatus.add(currentUserId);
                                Log.d("WaitListRepository", "added user to usersWithStatus "+ currentUserId);
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

    public interface WaitListCallback {
        void onSuccess(WaitList waitList);
        void onFailure(Exception e);
    }

    public interface FirestoreCallback {
        void onSuccess(Object result);
        void onFailure(Exception e);
    }
}