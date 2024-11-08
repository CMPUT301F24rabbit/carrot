package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.model.notification.Notification;

/**
 * Notification controller provides all the methods to update
 * the Notification model
 */
public class NotificationController {
    Notification notification;


    public NotificationController(){
        // Singleton
        this.notification = createNorification();
    }

    /**
     * Creates a new notification model and returns it
     * @return Notification model
     */
    public Notification createNorification(){
        notification = new Notification();

        notification.setEventId("test");
        notification.setMessage("test message");
        notification.setStatus("accepted");
        notification.setWaitListId("waitlistId");


        return notification;
    }

    public Notification getNotification(){
        return this.notification;
    }

}
