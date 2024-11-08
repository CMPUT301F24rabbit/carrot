package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationUtils;

/**
 * Notification controller provides all the methods to update
 * the Notification model
 */
public class NotificationController {
    Notification notification;


    public NotificationController(){
        // Singleton
        this.notification = createNotification();
    }

    /**
     * Creates a new notification model and returns it
     * @return Notification model
     */
    public Notification createNotification(){
        return notification;
    }

    /**
     *  Creates notification for Not chosen entrants
     * @param userId of the user
     * @param eventId related to the notification
     * @param waitListId related to the notification
     * @return notification
     */
    public Notification getOrCreateNotChosenNotification(final String userId,
                                                         final String eventId,
                                                         final String waitListId){
        return  new Notification(userId, eventId, waitListId, null,
                NotificationUtils.NOT_CHOSEN_MESSAGE, NotificationUtils.NOT_CHOSEN);
    }

    /**
     *  Creates notification for chosen entrants
     * @param userId of the user
     * @param eventId related to the notification
     * @param waitListId related to the notification
     * @return notification
     */
    public Notification getOrCreateChosenNotification(final String userId,
                                                         final String eventId,
                                                         final String waitListId){
        return new Notification(userId, eventId, waitListId, null,
                NotificationUtils.CHOSEN_MESSAGE, NotificationUtils.CHOSEN
        );
    }

    public Notification getNotification(){
        return this.notification;
    }

}
