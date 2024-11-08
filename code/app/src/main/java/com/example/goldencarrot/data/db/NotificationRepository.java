package com.example.goldencarrot.data.db;

import com.example.goldencarrot.data.model.notification.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the necessary methods to write, delete, update,
 * and fetch notifications from the notifications table in Firebase
 */
public class NotificationRepository {
    private final CollectionReference notificationsCollection;

    /**
     * Constructs a NotificationRepository object, initializing the Firestore collection.
     *
     * @param db FirebaseFirestore instance used to interact with Firestore.
     */
    public NotificationRepository(FirebaseFirestore db) {
        this.notificationsCollection = db.collection("notifications");
    }

    /**
     * Callback interface to handle results or failures of notification operations.
     *
     * @param <T> The type of result returned by the operation.
     */
    public interface NotificationCallback<T> {
        /**
         * Called when the operation is successful.
         *
         * @param result The result of the operation.
         */
        void onSuccess(T result);

        /**
         * Called when the operation fails.
         *
         * @param e The exception that caused the failure.
         */
        void onFailure(Exception e);
    }

    /**
     * Adds a new notification to Firestore and uses the callback to notify success or failure.
     *
     * @param notification The notification to be added.
     * @param callback The callback to be invoked on success or failure.
     */
    public void addNotification(Notification notification, NotificationCallback<Notification> callback) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", notification.getUserId());
        notificationData.put("eventId", notification.getEventId());
        notificationData.put("waitListId", notification.getWaitListId());
        notificationData.put("message", notification.getMessage());
        notificationData.put("status", notification.getStatus());

        notificationsCollection.add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    notification.setNotificationId(documentReference.getId());
                    callback.onSuccess(notification);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves a notification by its unique ID.
     *
     * @param notificationId The ID of the notification to retrieve.
     * @param callback The callback to handle the retrieved notification or failure.
     */
    public void getNotification(String notificationId, NotificationCallback<Notification> callback) {
        notificationsCollection.document(notificationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Notification notification = documentToNotification(documentSnapshot);
                        callback.onSuccess(notification);
                    } else {
                        callback.onFailure(new Exception("Notification not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Updates a notification in Firestore with the provided updates.
     *
     * @param notificationId The ID of the notification to update.
     * @param updates The map of fields to update in the notification.
     * @param callback The callback to handle success or failure.
     */
    public void updateNotification(String notificationId, Map<String, Object> updates, NotificationCallback<Boolean> callback) {
        notificationsCollection.document(notificationId).update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Deletes a notification by its unique ID.
     *
     * @param notificationId The ID of the notification to delete.
     * @param callback The callback to handle success or failure.
     */
    public void deleteNotification(String notificationId, NotificationCallback<Boolean> callback) {
        notificationsCollection.document(notificationId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all notifications associated with a specific user ID.
     *
     * @param userId The user ID to query for notifications.
     * @param callback The callback to handle the list of notifications or failure.
     */
    public void getNotificationsByUserId(String userId, NotificationCallback<List<Notification>> callback) {
        Query query = notificationsCollection.whereEqualTo("userId", userId);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<Notification> notifications = new ArrayList<>();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Notification notification = documentToNotification(document);
                        notifications.add(notification);
                    }
                }
                callback.onSuccess(notifications);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Helper method to convert a DocumentSnapshot from Firestore to a Notification object.
     *
     * @param document The Firestore document to convert.
     * @return The converted Notification object.
     */
    private Notification documentToNotification(DocumentSnapshot document) {
        Notification notification = new Notification();
        notification.setNotificationId(document.getId());
        notification.setUserId(document.getString("userId"));
        notification.setEventId(document.getString("eventId"));
        notification.setWaitListId(document.getString("waitListId"));
        notification.setMessage(document.getString("message"));
        notification.setStatus(document.getString("status"));
        return notification;
    }
}

