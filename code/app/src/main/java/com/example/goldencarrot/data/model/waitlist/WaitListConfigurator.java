package com.example.goldencarrot.data.model.waitlist;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;

import java.util.ArrayList;

/**
 *  The {@code WaitListConfigurator} interface defines the behavior of a WaitList.
 *  It provides methods to retrieve and update the associated event and the list of users on the waitlist.
 */
public interface WaitListConfigurator {

    /**
     * Retrieves the list of users currently on the waitlist.
     *
     * @return the list of users on the waitlist
     */
    ArrayList<UserImpl> getUserArrayList();

    /**
     * Sets the list of users on the waitlist.
     *
     * @param userArrayList the new list of users on the waitlist
     */
    void setUserArrayList(ArrayList<UserImpl> userArrayList);

    /**
     * Retrieves the event associated with the waitlist.
     *
     * @return the event associated with the waitlist
     */
    Event getEvent();

    /**
     * Sets the event associated with the waitlist.
     *
     * @param event the new event associated with the waitlist
     */
    void setEvent(Event event);

    /**
     * Sets the limit number for the waitlist.
     *
     * @param limitNumber the maximum number of users allowed on the waitlist
     */
    void setLimitNumber(int limitNumber);

    /**
     * Gets the limit number for the waitlist.
     *
     * @return the maximum number of users allowed on the waitlist
     */
    int getLimitNumber();

    /**
     * Adds a user to the waitlist if it is not full.
     *
     * @param user the user to be added to the waitlist
     * @return true if the user was added, false if the waitlist is full
     */
    boolean addUserToWaitList(UserImpl user);
}
