package com.example.goldencarrot.data.model.waitlist;

import com.example.goldencarrot.data.model.user.UserImpl;

import java.util.Map;

/**
 * The {@code WaitList} class represents a waiting list for an event.
 * It stores the event associated with the waitlist and the list of users
 * who are on the waitlist.
 * This class provides functionality to add users to the waitlist, remove users,
 * and check if the waitlist has reached its limit.
 */
public class WaitList implements WaitListConfigurator {

    /**
     * Waitlist id associated with the record in firebase
     */
    private String waitListId;

    /**
     * Event id associated with the event record in firebase
     */
    private String eventId;

    /**
     * Event name of the associated event
     */
    private String eventName;

    /**
     * Limit Number of the waitlist, can be null and optionally set by the organizer.
     */
    private int limitNumber;

    /**
     * Each key represnts a device id
     * the value represents the status in the waitlist
     */
    private Map<String, String> userMap;

    /**
     * Default constructor for initializing an empty waitlist.
     * Initializes the user list as an empty ArrayList.
     */
    public WaitList() {}

    /**
     * Constructor to initialize the waitlist with specified parameters.
     *
     * @param limitNumber the maximum number of users allowed on the waitlist.
     * @param waitListId the unique ID of the waitlist.
     * @param eventId the ID of the event associated with the waitlist.
     * @param eventName the name of the associated event.
     * @param userMap the list of users currently on the waitlist.
     */
    public WaitList(final int limitNumber,
                    final String waitListId,
                    final String eventId,
                    final String eventName,
                    final Map<String, String> userMap) {
        this.limitNumber = limitNumber;
        this.waitListId = waitListId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.userMap = userMap;
    }

    /**
     * Sets the limit number of users that can be on the waitlist.
     *
     * @param limitNumber the maximum number of users allowed on the waitlist.
     */
    @Override
    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }

    /**
     * Returns the limit number of users allowed on the waitlist.
     *
     * @return the limit number of users.
     */
    @Override
    public int getLimitNumber() {
        return limitNumber;
    }

    /**
     * Sets the event ID associated with the waitlist.
     *
     * @param eventId the ID of the event.
     */
    @Override
    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the event ID associated with the waitlist.
     *
     * @return the event ID as a String.
     */
    @Override
    public String getEventId() {
        return this.eventId;
    }

    /**
     * Returns the unique ID of the waitlist.
     *
     * @return the waitlist ID as a String.
     */
    @Override
    public String getWaitListId() {
        return waitListId;
    }

    /**
     * Sets the unique ID of the waitlist.
     *
     * @param waitListId the ID to set for the waitlist.
     */
    @Override
    public void setWaitListId(String waitListId) {
        this.waitListId = waitListId;
    }

    /**
     * Checks if the waitlist has reached its limit.
     *
     * @return {@code true} if the waitlist is full, {@code false} otherwise.
     */
    @Override
    public boolean isFull() {
        return this.userMap.size() == this.limitNumber;
    }

    /**
     * Removes a user from the waitlist.
     *
     * @param user the user to remove from the waitlist.
     */
    @Override
    public void removeUserFromWaitList(UserImpl user) {
        this.userMap.remove(user);
    }

    /**
     * Gets the user map of the waitlist
     * @return user map with the device id as key and the status as the value
     */
    @Override
    public Map<String, String> getUserMap() {
        return userMap;
    }

    /**
     * Sets the user map of the waitlist
     * @param userMap user map with the device id as key and the status as the value
     */
    @Override
    public void setUserMap(Map<String, String> userMap) {
        this.userMap = userMap;
    }

    /**
     * Gets the event name of the associated event object.
     * @return event name
     */
    @Override
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the event name of the associated event
     * @param eventName name of the associated event
     */
    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
