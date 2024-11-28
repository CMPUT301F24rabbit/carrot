package com.example.goldencarrot.data.db;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.util.Log;

import com.example.goldencarrot.data.model.event.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EventRepository {
    private static final String TAG = "EventRepository";
    public static EventRepository.EventCallback EventCallback;
    private FirebaseFirestore db;
    private CollectionReference eventsCollection;

    public EventRepository() {
        db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
    }

    public void addEvent(Event event, String posterUri, EventCallback callback) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", event.getEventName());
        eventData.put("eventDetails", event.getEventDetails());
        eventData.put("location", event.getLocation());

        eventsCollection.add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    event.setEventId(generatedId);

                    if (posterUri != null) {
                        uploadEventPoster(Uri.parse(posterUri), generatedId, new FirebasePosterCallback() {
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

    public void updateEvent(Event event, String posterUri, EventCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("eventName", event.getEventName());
        updates.put("eventDetails", event.getEventDetails());
        updates.put("location", event.getLocation());

        eventsCollection.document(event.getEventId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (posterUri != null) {
                        uploadEventPoster(Uri.parse(posterUri), event.getEventId(), new FirebasePosterCallback() {
                            @Override
                            public void onSuccess(String url) {
                                eventsCollection.document(event.getEventId()).update("posterUrl", url)
                                        .addOnSuccessListener(aVoid1 -> callback.onSuccess(event))
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
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("posters/" + eventId + "_poster.jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }

    public void getBasicEventById(String eventId, EventCallback callback) {
        eventsCollection.document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        callback.onSuccess(event);
                    } else {
                        callback.onFailure(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
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


