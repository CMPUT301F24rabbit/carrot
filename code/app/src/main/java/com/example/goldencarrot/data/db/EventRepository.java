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
     * Creates a new event document in Firestore
     * @param event is the event to be added
     */
    public void addEvent(Event event, @Nullable Integer waitlistLimit) {
        Map<String, Object> eventData = new HashMap<>();

        // Add event attributes to Firestore
        eventData.put("organizerId", event.getOrganizerId());
        eventData.put("eventDetails", event.getEventDetails());
        eventData.put("eventName", event.getEventName());
        eventData.put("location", event.getLocation());
        eventData.put("isGeolocationEnabled", event.getGeolocationEnabled());
        eventData.put("date", new SimpleDateFormat("dd-MM-yyyy").format(event.getDate()));

        // Add event document into events collection and capture the generated ID
        eventsCollection.add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String generatedId = documentReference.getId();
                    event.setEventId(generatedId); // Update the event object with the new ID
                    Log.d(TAG, "Event created successfully with ID: " + generatedId);

                    documentReference.update("eventId", generatedId)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Event ID updated in Firestore"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating event ID", e));

                    WaitListRepository waitListRepository = new WaitListRepository();
                    WaitList waitList = new WaitList();

                    // Set the limit only if specified by the organizer; otherwise, leave as null
                    if (waitlistLimit != null) {
                        waitList.setLimitNumber(waitlistLimit);
                    }
                    waitList.setEventId(event.getEventId());
                    waitList.setUserMap(new HashMap<String, String>());
                    waitListRepository.createWaitList(waitList, waitList.getWaitListId(), event.getEventName());

                })
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

    /**
     * Retrieves an event document from Firestore by its ID and returns a
     * simplified Event Model
     * object with essential attributes only
     *
     * @param eventId The ID of the event to retrieve
     * @param callback A callback to handle the Event data or error
     */
    public void getBasicEventById(String eventId, EventCallback callback) {
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

                        // checks if geolocation is enabled
                        event.setGeolocationEnabled(Boolean.TRUE.equals(documentSnapshot.
                                getBoolean("isGeolocationEnabled")));
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

    /**
     * Callback interface for Firestore event retrieval.
     */
    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }
}
