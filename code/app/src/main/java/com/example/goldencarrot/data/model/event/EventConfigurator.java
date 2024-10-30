package com.example.goldencarrot.data.model.event;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

public interface EventConfigurator {

    /**
     * Retrieves the organizer of the event.
     *
     * @return the organizer (UserImpl) of the event
     */
    UserImpl getOrganizer();

    /**
     * Sets the organizer of the event.
     *
     * @param organizer the UserImpl object to set as the organizer
     */
    void setOrganizer(UserImpl organizer);

    /**
     * Retrieves the waitlist of the event.
     *
     * @return the waitlist (WaitList) of the event
     */
    WaitList getWaitList();

    /**
     * Sets the waitlist of the event.
     *
     * @param waitList the WaitList object to set for the event
     */
    void setWaitList(WaitList waitList);
}
