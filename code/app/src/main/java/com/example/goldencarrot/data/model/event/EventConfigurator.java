package com.example.goldencarrot.data.model.event;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.Date;

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

    /**
     * Retrieves the name of the event.
     *
     * @return the name of the event
     */
    String getEventName();

    /**
     * Sets the name of the event.
     *
     * @param eventName the name to set for the event
     */
    void setEventName(String eventName);

    /**
     * Retrieves the details of the event.
     *
     * @return the details of the event
     */
    String getEventDetails();

    /**
     * Sets the details of the event.
     *
     * @param eventDetails the details to set for the event
     */
    void setEventDetails(String eventDetails);
    /**
     * Sets the location for the event.
     *
     * @param location The location of the event.
     */
    void setLocation(String location);

    /**
     * Retrieves the location of the event.
     *
     * @return The location of the event as a String.
     */
    String getLocation();

    /**
     * Sets the date for the event.
     *
     * @param date The date of the event.
     */
    void setDate(Date date);

    /**
     * Retrieves the date of the event.
     *
     * @return The date of the event as a Date object.
     */
    Date getDate();

    /**
     * Gets the waitlist ID associated with the event.
     *
     * @return the waitlist ID
     */
    String getWaitListId();

    /**
     * Sets the waitlist ID for the event.
     *
     * @param waitListId the waitlist ID to set
     */
    void setWaitListId(String waitListId);

    /**
     * Gets the organizer ID associated with the event.
     *
     * @return the organizer ID
     */
    String getOrganizerId();

    /**
     * Sets the organizer ID for the event.
     *
     * @param organizerId the organizer ID to set
     */
    void setOrganizerId(String organizerId);

    void setEventId(String eventId);

    String getEventId();

    /**
     * Retrieves the waitlist limit for the event, if any.
     *
     * @return the waitlist limit as an Integer, or null if no limit is set
     */
    Integer getWaitlistLimit();

    /**
     * Sets an optional limit for the number of entrants on the waitlist.
     *
     * @param waitlistLimit the maximum number of entrants allowed on the waitlist, or null for unlimited
     */
    void setWaitlistLimit(Integer waitlistLimit);

    /**
     *  Returns weather geolocation is enabled or not
     * @return geolocationEnabled bool
     */
    boolean getGeolocationEnabled();

    /**
     * Can set geolocation enabled true or false
     * @param geolocationEnabled boolean
     */
    void setGeolocationEnabled(boolean geolocationEnabled);
    /**
     * Retrieves the poster URL for the event.
     *
     * @return the URL of the event poster as a String.
     */
    String getPosterUrl();

    /**
     * Sets the poster URL for the event.
     *
     * @param url the URL to set for the event poster.
     */
    void setPosterUrl(Object url);

}
