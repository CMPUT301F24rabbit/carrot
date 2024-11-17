package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationUtils;

/**
 * Notification Controller provides all the methods to update
 * the Notification Model
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

    /**
     * Creates notification for a single User
     * @param userId of the user to receiving notification
     */
    public Notification getOrCreateNotification(final String userId) {
        return new Notification(userId, null, null, null,
                NotificationUtils.SINGLE_USER_MESSAGE, NotificationUtils.SINGLE_USER);
    }

    public void changeNotificationStatus(final Notification notification, final String status) throws Exception {
        if (!NotificationUtils.validNotificationStatus.contains(status)){
            throw new Exception("Invalid Notification Status");
        }

        notification.setStatus(status);
    }
}
