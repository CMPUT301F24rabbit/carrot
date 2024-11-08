package com.example.goldencarrot.data.model.notification;

/**
 * Notification Class
 *
 * Provides all the methods for notifications
 */
public class Notification {
    private String userId;
    private String eventId;
    private String waitListId;
    private String notificationId;
    private String message;
    private String status;

    public Notification(){}

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter for eventId
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    // Getter and Setter for waitListId
    public String getWaitListId() {
        return waitListId;
    }

    public void setWaitListId(String waitListId) {
        this.waitListId = waitListId;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for notificationid
    public String getNotificationId() {
        return status;
    }

    public void setNotificationId(String status) {
        this.status = status;
    }
}
