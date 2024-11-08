package com.example.goldencarrot.data.model.waitlist;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * The {@code WaitList} class represents a waiting list for an event.
 * It stores the event associated with the waitlist and the list of users
 * who are on the waitlist.
 *
 * This class provides functionality to add users to the waitlist, remove users,
 * and check if the waitlist has reached its limit.
 */
public class WaitList implements WaitListConfigurator {

    private String waitListId;
    private String eventId;
    private int limitNumber;
    private ArrayList<UserImpl> userArrayList;

    /**
     * Default constructor for initializing an empty waitlist.
     * Initializes the user list as an empty ArrayList.
     */
    public WaitList() {
        // Initialize Array List
        this.userArrayList = new ArrayList<UserImpl>();
    }

    /**
     * Constructor to initialize the waitlist with specified parameters.
     *
     * @param limitNumber the maximum number of users allowed on the waitlist.
     * @param waitListId the unique ID of the waitlist.
     * @param eventId the ID of the event associated with the waitlist.
     * @param userArrayList the list of users currently on the waitlist.
     */
    public WaitList(final int limitNumber,
                    final String waitListId,
                    final String eventId,
                    final ArrayList<UserImpl> userArrayList) {
        this.limitNumber = limitNumber;
        this.waitListId = waitListId;
        this.eventId = eventId;
        this.userArrayList = userArrayList;
    }

    /**
     * Returns the list of users currently on the waitlist.
     *
     * @return the list of {@code UserImpl} objects.
     */
    @Override
    public ArrayList<UserImpl> getUserArrayList() {
        return userArrayList;
    }

    /**
     * Sets the list of users for the waitlist.
     *
     * @param userArrayList the list of users to set.
     */
    @Override
    public void setUserArrayList(ArrayList<UserImpl> userArrayList) {
        this.userArrayList = userArrayList;
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
     * Adds a user to the waitlist if the list is not full.
     *
     * If the waitlist is full, the user is not added, and {@code false} is returned.
     * If the waitlist is not full, the user is added, and {@code true} is returned.
     *
     * @param user the user to add to the waitlist.
     * @return {@code true} if the user was added, {@code false} if the waitlist is full.
     */
    @Override
    public boolean addUserToWaitList(final UserImpl user) {
        if (isFull()) {
            return Boolean.FALSE;
        } else {
            userArrayList.add(user);
            return Boolean.TRUE;
        }
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
    public boolean isFull() {
        return this.userArrayList.size() == this.limitNumber;
    }

    /**
     * Removes a user from the waitlist.
     *
     * @param user the user to remove from the waitlist.
     */
    public void removeUserFromWaitList(UserImpl user) {
        this.userArrayList.remove(user);
    }
}
