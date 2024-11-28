package com.example.goldencarrot.controller;

import android.net.Uri;

import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

/**
 * Controller for managing Event operations.
 */
public class EventController {
    private EventRepository eventRepository;

    public EventController() {
        this.eventRepository = new EventRepository();
    }

    public void uploadEventPoster(Uri imageUri, String eventId, EventRepository.FirebasePosterCallback  callback) {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("_posters/" + eventId + "_poster.jpg");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String posterUrl = uri.toString();
                callback.onSuccess(posterUrl);
            }).addOnFailureListener(e -> callback.onFailure(e));
        }).addOnFailureListener(e -> callback.onFailure(e));
    }
}




