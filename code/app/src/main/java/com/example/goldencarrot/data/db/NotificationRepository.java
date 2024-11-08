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
 * This class provides the necessary methods to write, delete
 * update, and fetch notifications from notifications table.
 */
public class NotificationRepository {
    private final CollectionReference notificationsCollection;

    public NotificationRepository(FirebaseFirestore db) {
        this.notificationsCollection = db.collection("notifications");
    }

    // Callback interface
    public interface NotificationCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    // Add a notification and use callback for result
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

    // Retrieve a notification by ID
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

    // Update a notification
    public void updateNotification(String notificationId, Map<String, Object> updates, NotificationCallback<Boolean> callback) {
        notificationsCollection.document(notificationId).update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    // Delete a notification
    public void deleteNotification(String notificationId, NotificationCallback<Boolean> callback) {
        notificationsCollection.document(notificationId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    // Reference: Google. (n.d.). Query data in Cloud Firestore. Firebase
    // Retrieved from https://firebase.google.com/docs/firestore/query-data/queries#java
    // Gets all the notifications for the user with the user id
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

    // Helper method to convert DocumentSnapshot to Notification model object
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

