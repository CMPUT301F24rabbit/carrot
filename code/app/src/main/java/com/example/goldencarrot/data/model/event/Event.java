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
    private String eventId;
    private String eventDetails;
    private String organizerId;
    private String waitListId;
    private Date date;
    private  int imageResId;
    private Integer waitlistLimit;

    public Event(){}

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
        this.waitlistLimit = waitlistLimit;

    }

    @Override
    public UserImpl getOrganizer() {
        return organizer;
    }

    /**
     * Sets Organizer object
     * @param organizer the UserImpl object to set as the organizer
     */
    @Override
    public void setOrganizer(UserImpl organizer) {
        this.organizer = organizer;
    }

    /**
     * @param waitList the WaitList object to set for the event
     */
    @Override
    public void setWaitList(WaitList waitList) {
        this.waitList = waitList;
    }

    /**
     * sets waitlist limit (optional for organizer)
     */
    // Getter and Setter for waitlistLimit
    public Integer getWaitlistLimit() {
        return waitlistLimit;
    }
    /**
     * return the waitlist limit (optional for organizer)
     */
    public void setWaitlistLimit(Integer waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }

    /**
     * @return event name
     */
    @Override
    public String getEventName() {
        return this.eventName;
    }

    /**
     * Sets Event's name
     * @param eventName the name to set for the event
     */
    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets event details
     * @return String event details
     */
    @Override
    public String getEventDetails() {
        return this.eventDetails;
    }

    /**
     * Sets Event Details
     * @param eventDetails the details to set for the event
     */
    @Override
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    /**
     * Sets the date the event is happening
     * @param date The date of the event.
     */
    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the date of the Event
     * @return date
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * Gets Event's location
     * @return location
     */
    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets Event's location
     * @return location
     */

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getWaitListId(){
        return this.waitListId;
    }

    @Override
    public void setWaitListId(String waitListId) {
        this.waitListId = waitListId;
    }

    @Override
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    @Override
    public String getOrganizerId() {
        return organizerId;
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
