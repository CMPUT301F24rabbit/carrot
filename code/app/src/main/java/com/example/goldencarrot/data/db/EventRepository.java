package com.example.goldencarrot.data.db;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class EventRepository {
    private static final String TAG = "EventRepository";

    private final FirebaseFirestore db;
    private final CollectionReference eventsCollection;

    public EventRepository() {
        db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
    }

    public void addEvent(Event event, @Nullable Uri posterUri, EventCallback callback) {
        if (event == null || callback == null) {
            Log.e(TAG, "Event or callback is null");
            callback.onFailure(new IllegalArgumentException("Invalid input parameters"));
            return;
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("organizerId", event.getOrganizerId());
        eventData.put("eventDetails", event.getEventDetails());
        eventData.put("eventName", event.getEventName());
        eventData.put("location", event.getLocation());
        eventData.put("isGeolocationEnabled", event.getGeolocationEnabled());
        eventData.put("date", new SimpleDateFormat("dd-MM-yyyy").format(event.getDate()));

        eventsCollection.add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    event.setEventId(generatedId);
                    documentReference.update("eventId", generatedId);

                    createWaitlist(event.getWaitlistLimit(), event, callback);

                    if (posterUri != null) {
                        uploadEventPoster(posterUri, generatedId, new FirebasePosterCallback() {
                            @Override
                            public void onSuccess(String url) {
                                documentReference.update("posterUrl", url)
                                        .addOnSuccessListener(aVoid -> callback.onSuccess(event))
                                        .addOnFailureListener(callback::onFailure);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                callback.onFailure(e);
                            }
                        });
                    } else {
                        callback.onSuccess(event);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void uploadEventPoster(Uri imageUri, String eventId, FirebasePosterCallback callback) {
        if (imageUri == null || eventId == null || callback == null) {
            Log.e(TAG, "Image URI, Event ID, or Callback is null");
            callback.onFailure(new IllegalArgumentException("Invalid input parameters"));
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("posters/" + eventId + "_poster.jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }
    public void getBasicEventById(String eventId, EventCallback callback) {
        if (eventId == null || callback == null) {
            Log.e(TAG, "Event ID or callback is null");
            return;
        }

        eventsCollection.document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = new Event();
                        event.setEventName(documentSnapshot.getString("eventName"));
                        event.setEventDetails(documentSnapshot.getString("eventDetails"));
                        event.setLocation(documentSnapshot.getString("location"));
                        event.setWaitListId(documentSnapshot.getString("waitlistId"));
                        event.setOrganizerId(documentSnapshot.getString("organizerId"));
                        event.setGeolocationEnabled(Boolean.TRUE.equals(documentSnapshot.getBoolean("isGeolocationEnabled")));
                        callback.onSuccess(event);
                    } else {
                        Log.w(TAG, "No event found with ID: " + eventId);
                        callback.onFailure(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error fetching event", e);
                    callback.onFailure(e);
                });
    }


    public void deleteEvent(String eventId) {
        if (eventId == null) {
            Log.e(TAG, "Event ID is null");
            return; // Handle the error gracefully
        }

        eventsCollection.document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event deleted successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event", e));
    }
    private void createWaitlist(@Nullable Integer waitlistLimit, Event event, EventCallback callback) {
        WaitListRepository waitListRepository = new WaitListRepository();
        WaitList waitList = new WaitList();

        if (waitlistLimit != null) {
            waitList.setLimitNumber(waitlistLimit);
        }

        waitList.setEventName(event.getEventName());
        waitList.setEventId(event.getEventId());
        waitList.setUserMap(new HashMap<>());

        waitListRepository.createWaitList(waitList, event.getEventName());
        callback.onSuccess(event);
    }

    public interface FirebasePosterCallback {
        void onSuccess(String url);
        void onFailure(Exception e);
    }

    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }
}
