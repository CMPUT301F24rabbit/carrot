package com.example.goldencarrot.data.model.event;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.Date;

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
    private String eventName;
    private String location;
    private String eventDetails;
    private Date date;
    private  int imageResId;

    public Event(final UserImpl organizer){
        this.organizer = organizer;
    }

    public Event(UserImpl organizer, String eventName, String location, Date date, String eventDetails, int imageResId) {
        this.organizer = organizer;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.eventDetails = eventDetails;
        this.imageResId = imageResId;
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
    public String getEventName() {
        return this.eventName;
    }

    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String getEventDetails() {
        return this.eventDetails;
    }

    @Override
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public WaitList getWaitList() {
        return waitList;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
