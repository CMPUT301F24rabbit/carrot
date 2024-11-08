package com.example.goldencarrot.data.model.user;

import java.util.Optional;

/**
 * Interface representing a User in the system.
 *
 * This interface defines the basic attributes and behaviors of a user, including their contact details, user type,
 * and notification preferences. The implementing class must provide methods for getting and setting these properties.
 * Todo : User controller class that validates user creation and deletion
 */
public interface User {

    /**
     * Returns the email address of the user.
     *
     * @return the user's email address as a String.
     */
    String getEmail();

    /**
     * Returns the type of user (e.g., Organizer, Admin, etc.).
     *
     * @return the type of the user as a String.
     */
    String getUserType();

    /**
     * Returns the name of the user.
     *
     * @return the user's name as a String.
     */
    String getName();

    /**
     * Returns the phone number of the user, if available.
     *
     * @return an Optional containing the user's phone number, or an empty Optional if no phone number is set.
     */
    Optional<String> getPhoneNumber();

    /**
     * Returns the unique user ID.
     *
     * @return the user's ID as a String.
     */
    String getUserId();

    /**
     * Returns whether the user has opted to receive admin notifications.
     *
     * @return true if the user has enabled admin notifications, false otherwise.
     */
    Boolean getAdminNotification();

    /**
     * Returns whether the user has opted to receive organizer notifications.
     *
     * @return true if the user has enabled organizer notifications, false otherwise.
     */
    Boolean getOrganizerNotifications();

    /**
     * Sets the unique user ID. Should be the Android Id
     *
     * @param uId the user ID to set.
     */
    void setUserId(String uId);

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber an Optional containing the user's phone number, or an empty Optional
     *                    if no phone number is provided
     */
    void setPhoneNumber(Optional<String> phoneNumber);
}
