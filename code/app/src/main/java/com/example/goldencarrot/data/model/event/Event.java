package com.example.goldencarrot.data.model.event;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.Date;
/**
 * The {@code Event} class represents an Event stored in the system.
 * It stores the Organizer User object that created the event.
 * This class provides methods to update and create Event objects.
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
    private int imageResId;
    private Integer waitlistLimit;
    private boolean isGeolocationEnabled;

    /**
     * Default constructor for the Event class.
     */
    public Event() {}

    /**
     * Constructor to initialize an Event with an Organizer object.
     *
     * @param organizer the UserImpl object representing the organizer of the event.
     */
    public Event(final UserImpl organizer) {
        this.organizer = organizer;
    }

    /**
     * Constructor to initialize an Event with its details such as name, location, date, and details.
     *
     * @param organizer the UserImpl object representing the organizer of the event.
     * @param eventName the name of the event.
     * @param location the location where the event will take place.
     * @param date the date of the event.
     * @param eventDetails the details or description of the event.
     * @param imageResId the resource ID for an image related to the event.
     */
    public Event(UserImpl organizer, String eventName, String location, Date date, String eventDetails, int imageResId) {
        this.organizer = organizer;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.eventDetails = eventDetails;
        this.imageResId = imageResId;
        this.waitlistLimit = waitlistLimit;
    }

    /**
     * Gets the Organizer of the event.
     *
     * @return the UserImpl object representing the organizer.
     */
    @Override
    public UserImpl getOrganizer() {
        return organizer;
    }

    /**
     * Sets the Organizer object for the event.
     *
     * @param organizer the UserImpl object to set as the organizer.
     */
    @Override
    public void setOrganizer(UserImpl organizer) {
        this.organizer = organizer;
    }

    /**
     * Sets the WaitList for the event.
     *
     * @param waitList the WaitList object to set for the event.
     */
    @Override
    public void setWaitList(WaitList waitList) {
        this.waitList = waitList;
    }

    /**
     * Gets the Waitlist limit for the event (optional for organizer).
     *
     * @return the limit of the waitlist for the event.
     */
    @Override
    public Integer getWaitlistLimit() {
        return waitlistLimit;
    }

    /**
     * Sets the Waitlist limit for the event (optional for organizer).
     *
     * @param waitlistLimit the number of participants allowed on the waitlist.
     */
    @Override
    public void setWaitlistLimit(Integer waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }

    /**
     * Gets the name of the event.
     *
     * @return the name of the event.
     */
    @Override
    public String getEventName() {
        return this.eventName;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName the name to set for the event.
     */
    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets the event details.
     *
     * @return a string describing the event.
     */
    @Override
    public String getEventDetails() {
        return this.eventDetails;
    }

    /**
     * Sets the details or description of the event.
     *
     * @param eventDetails the details to set for the event.
     */
    @Override
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    /**
     * Sets the date when the event is scheduled to occur.
     *
     * @param date the date of the event.
     */
    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the date when the event is scheduled to occur.
     *
     * @return the date of the event.
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * Sets the unique identifier for the event.
     *
     * @param eventId the ID to set for the event.
     */
    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the unique identifier for the event.
     *
     * @return the ID of the event.
     */
    @Override
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets the location where the event will take place.
     *
     * @return the location of the event.
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location where the event will take place.
     *
     * @param location the location of the event.
     */
    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the unique identifier for the event's waitlist.
     *
     * @return the waitlist ID associated with the event.
     */
    @Override
    public String getWaitListId() {
        return this.waitListId;
    }

    /**
     * Sets the unique identifier for the event's waitlist.
     *
     * @param waitListId the ID to set for the event's waitlist.
     */
    @Override
    public void setWaitListId(String waitListId) {
        this.waitListId = waitListId;
    }

    /**
     * Sets the unique identifier for the organizer of the event.
     *
     * @param organizerId the ID to set for the organizer.
     */
    @Override
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Gets the unique identifier for the organizer of the event.
     *
     * @return the ID of the organizer.
     */
    @Override
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Gets the WaitList object associated with the event.
     *
     * @return the WaitList object.
     */
    @Override
    public WaitList getWaitList() {
        return waitList;
    }

    /**
     * Gets the resource ID of the image associated with the event.
     *
     * @return the image resource ID.
     */
    public int getImageResId() {
        return imageResId;
    }

    /**
     * Sets the resource ID of the image associated with the event.
     *
     * @param imageResId the image resource ID to set.
     */
    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    /**
     * Checks whether geolocation is enabled for the event.
     *
     * @return true if geolocation is enabled, false otherwise.
     */
    @Override
    public boolean getGeolocationEnabled() {
        return isGeolocationEnabled;
    }

    /**
     * Sets whether geolocation is enabled for the event.
     *
     * @param geolocationEnabled true to enable geolocation, false to disable.
     */
    @Override
    public void setGeolocationEnabled(boolean geolocationEnabled) {
        this.isGeolocationEnabled = geolocationEnabled;
    }
}
