package com.example.goldencarrot.data.model.waitlist;

import com.example.goldencarrot.data.model.user.UserImpl;

import java.util.ArrayList;
import java.util.Map;

/**
 * The {@code WaitListConfigurator} interface defines the behavior of a WaitList.
 * It provides methods to retrieve and update the associated event and the list of users on the waitlist.
 */
public interface WaitListConfigurator {

    /**
     * Sets the limit number for the waitlist.
     *
     * @param limitNumber the maximum number of users allowed on the waitlist
     */
    void setLimitNumber(final int limitNumber);

    /**
     * Gets the limit number for the waitlist.
     *
     * @return the maximum number of users allowed on the waitlist
     */
    int getLimitNumber();

    /**
     * Removes a user from the waitlist.
     *
     * @param user the user to remove from the waitlist
     */
    void removeUserFromWaitList(final UserImpl user);

    /**
     * Checks if the waitlist is full.
     *
     * @return true if the waitlist is full, false otherwise
     */
    boolean isFull();

    /**
     * Gets the Event ID associated with this waitlist.
     *
     * @return Event ID
     */
    String getEventId();

    /**
     * Sets the Event ID related to an event record in the events database.
     *
     * @param eventId the related Event ID
     */
    void setEventId(final String eventId);

    /**
     * Gets the waitlist ID.
     *
     * @return the waitlist ID
     */
    String getWaitListId();

    /**
     * Sets the waitlist ID.
     *
     * @param waitListId the waitlist ID to set
     */
    void setWaitListId(String waitListId);

    /**
     * Gets the user map of the waitlist, where the key is a device ID,
     * and the value is the user's status in the waitlist.
     *
     * @return the user map
     */
    Map<String, String> getUserMap();

    /**
     * Sets the user map of the waitlist.
     *
     * @param userMap the user map to set, with device IDs as keys and statuses as values
     */
    void setUserMap(Map<String, String> userMap);

    /**
     * Gets the name of the event associated with the waitlist.
     *
     * @return the event name
     */
    String getEventName();

    /**
     * Sets the name of the event associated with the waitlist.
     *
     * @param eventName the event name to set
     */
    void setEventName(String eventName);
}
