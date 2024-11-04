package com.example.goldencarrot.data.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Queries Event DB. Provides methods for getting an event's waitlist,
 * List of all cancelling entrants, and the final list of entrants
 */
public class EventRepository {
    private static final String TAG = "EventRepository";
    private FirebaseFirestore db;
    private CollectionReference eventsCollection;
    private CollectionReference userCollection;

    /**
     * constructs a new {@code EventRepository} with Firebase instance
     */
    public EventRepository() {
        db = FirebaseFirestore.getInstance();
        eventsCollection = db.collection("events");
        userCollection = db.collection("users");
    }
    /**
     * creates a new event document in Firestore
     * @param event is the event to be added
     */
    public void addEvent(Event event) {
        Map<String, Object> eventData = new HashMap<>();

        // add event attributes to firestore
        eventData.put("organizer", event.getOrganizer().getUserId());
        eventData.put("waitlist", event.getWaitList());
        eventData.put("event details", event.getEventDetails());
        eventData.put("location", event.getLocation());
        eventData.put("date", event.getDate());

        // add event document into events collection
        eventsCollection.document(event.getEventName())
                .set(eventData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event created successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating event", e));
    }
    /**
     * deletes event from Firestore
     * @param eventId is the event to delete
     */
    public void deleteEvent(String eventId) {
        eventsCollection.document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event deleted successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event", e));
    }
    
}
