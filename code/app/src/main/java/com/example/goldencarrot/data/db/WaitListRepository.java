package com.example.goldencarrot.data.db;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.util.Log;

import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
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
     * @param docId    the document ID for this waitlist in Firestore
     */
    @Override
    public void createWaitList(WaitList waitList, String docId, String eventName) {
        Log.d("WaitListRepository", "creating waitlist");
        Map<String, Object> waitListData = new HashMap<>();
        waitListData.put("eventId", waitList.getEventId());
        waitListData.put("eventName", eventName);
        waitListData.put("limit", waitList.getLimitNumber());
        waitListData.put("size", waitList.getUserArrayList().size());
        Log.d("WaitListRepository", "creating waitlist for accepted, declined, waiting");

        // Create a "users" sub-map to store user statuses
        Map<String, String> usersMap = new HashMap<>();
        for (UserImpl user : waitList.getUserArrayList()) {
            usersMap.put(user.getUserId(), "waiting");  // Default status to "waiting"
        }
        Log.d("WaitListRepository", "putting map");
        waitListData.put("users", usersMap);  // Add users map to the main document

        // Add the waitlist document to Firestore
        waitListRef.document(docId)
                .set(waitListData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "WaitList created successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating waitlist", e));
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
                                usersMap.put(user.getUserId(), "waiting");
                                updateData.put("users", usersMap);  // Add the new user in the new map
                            } else {
                                // Add the user to the existing "users" map
                                updateData.put("users." + user.getUserId(), "waiting");
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
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching waitlist", e);
                    callback.onFailure(e);
                });
    }


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
                        Map<String, Object> usersMap = (Map<String, Object>) document.get("users");
                        if (usersMap != null) {
                            for (String userId : usersMap.keySet()) {
                                UserImpl user = new UserImpl();
                                user.setUserId(userId);
                                userArrayList.add(user);
                            }
                        }

                        WaitList waitList = new WaitList(
                                // Waitlist limit
                                document.getLong("limit").intValue(),
                                // Waitlist document Id
                                document.getId(),
                                // Event Id
                                eventId,
                                // User Array List
                                userArrayList
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

    @Override
    public void getUserStatus(String docId, UserImpl user, FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("users." + user.getUserId())) {
                        String status = documentSnapshot.getString("users." + user.getUserId());
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

    @Override
    public void getUsersWithStatus(final String docId, final String status, final FirestoreCallback callback) {
        waitListRef.document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> usersWithStatus = new ArrayList<>();

                        // get users document
                        Map<String, Object> usersData = (Map<String, Object>) documentSnapshot.get("users");
                        if (usersData != null) {
                            for (Map.Entry<String, Object> entry : usersData.entrySet()) {
                                if (entry.getValue().toString().equals(status)) {
                                    usersWithStatus.add(entry.getKey());
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

    public interface WaitListCallback {
        void onSuccess(WaitList waitList);
        void onFailure(Exception e);
    }

    public interface FirestoreCallback {
        void onSuccess(Object result);
        void onFailure(Exception e);
    }
}
