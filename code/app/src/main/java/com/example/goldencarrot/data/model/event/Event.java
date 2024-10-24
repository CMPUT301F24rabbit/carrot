package com.example.goldencarrot.data.model.event;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

/**
 *  The {@code Event} Class represents an Event stored in the system
 *  It stores the Organizer User Object that created the event
 *  <p>
 *      This class provides implements methods to update and
 *      create Event Objects.
 *  </p>
 */
public class Event implements EventConfigurator {
    private UserImpl organizer;
    private WaitList waitList;

    public Event(final UserImpl organizer){
        this.organizer = organizer;
    }

    @Override
    public UserImpl getOrganizer() {
        return organizer;
    }

    @Override
    public void setOrganizer(UserImpl organizer) {
        this.organizer = organizer;
    }

    @Override
    public void setWaitList(WaitList waitList) {
        this.waitList = waitList;
    }

    @Override
    public WaitList getWaitList() {
        return waitList;
    }
}
