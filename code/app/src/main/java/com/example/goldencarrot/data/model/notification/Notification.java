package com.example.goldencarrot.data.model.notification;

/**
 * Notification Class
 * Represents a notification associated with an event, waitlist, and user.
 * Provides methods to access and modify the notification's details.
 */
public class Notification {
    private String userId;
    private String eventId;
    private String waitListId;
    private String notificationId;
    private String message;
    private String status;

    /**
     * Default constructor for creating a Notification object without setting any properties.
     * This constructor is typically used for testing purposes.
     */
    public Notification() {}

    /**
     * Constructor to create a Notification with specified values for all properties
     *
     * @param userId the ID of the user associated with the notification
     * @param eventId the ID of the event associated with the notification
     * @param waitListId the ID of the waitlist associated with the notification
     * @param notificationId the unique ID of the notification
     * @param message the message content of the notification
     * @param status the status of the notification (e.g., read/unread)
     */
    public Notification(final String userId, final String eventId,
                        final String waitListId, final String notificationId,
                        final String message, final String status) {
        this.userId = userId;
        this.eventId = eventId;
        this.waitListId = waitListId;
        this.notificationId = notificationId;
        this.message = message;
        this.status = status;
    }

    /**
     * Gets the ID of the user associated with this notification.
     *
     * @return the user android ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user associated with this notification.
     *
     * @param userId the user ID to be set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the event associated with this notification.
     *
     * @return the event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event associated with this notification.
     *
     * @param eventId the event ID to be set.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the ID of the waitlist associated with this notification.
     *
     * @return the waitlist ID.
     */
    public String getWaitListId() {
        return waitListId;
    }

    /**
     * Sets the ID of the waitlist associated with this notification.
     *
     * @param waitListId the waitlist ID to be set.
     */
    public void setWaitListId(String waitListId) {
        this.waitListId = waitListId;
    }

    /**
     * Gets the message content of this notification.
     *
     * @return the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content for this notification.
     *
     * @param message the message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the status of this notification eg. chosen, notchosen etc
     *
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of this notification eg. chosen or notchosen
     *
     * @param status the status to be set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the unique Id of this notification
     *
     * @return the notification ID
     */
    public String getNotificationId() {
        return this.notificationId;
    }

    /**
     * Sets the unique ID for this notification
     *
     * @param notificationId the notification ID to be set.
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}

