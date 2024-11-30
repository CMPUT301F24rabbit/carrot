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


}




