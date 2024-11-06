package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import java.util.Date;

public class EventController {
    private Event event;

    public EventController(UserImpl organizer) {
        this.event = new Event(organizer);
    }

    /**
     * Initializes a new event with the provided organizer.
     *
     * @param organizer The UserImpl object representing the event organizer.
     */
    public void initializeEvent(UserImpl organizer) {
        this.event = new Event(organizer);
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName The name of the event.
     */
    public void setEventName(String eventName) {
        event.setEventName(eventName);
    }

    /**
     * Retrieves the name of the event.
     *
     * @return The event's name.
     */
    public String getEventName() {
        return event.getEventName();
    }

    /**
     * Sets the location for the event.
     *
     * @param location The location of the event.
     */
    public void setLocation(String location) {
        event.setLocation(location);
    }

    /**
     * Retrieves the event location.
     *
     * @return The location of the event.
     */
    public String getLocation() {
        return event.getLocation();
    }

    /**
     * Sets additional details for the event.
     *
     * @param eventDetails The details of the event.
     */
    public void setEventDetails(String eventDetails) {
        event.setEventDetails(eventDetails);
    }

    /**
     * Retrieves the event details.
     *
     * @return The details of the event.
     */
    public String getEventDetails() {
        return event.getEventDetails();
    }

    /**
     * Sets the date for the event.
     *
     * @param date The date of the event.
     */
    public void setDate(Date date) {
        event.setDate(date);
    }

    /**
     * Retrieves the event date.
     *
     * @return The date of the event.
     */
    public Date getDate() {
        return event.getDate();
    }

    /**
     * Sets a waitlist for the current event.
     *
     * @param waitList The WaitList object to be associated with the event.
     */
    public void setEventWaitList(WaitList waitList) {
        event.setWaitList(waitList);
    }

    /**
     * Retrieves the current waitlist for the event.
     *
     * @return The WaitList object associated with the event.
     */
    public WaitList getEventWaitList() {
        return event.getWaitList();
    }

    /**
     * Updates the organizer for the event.
     *
     * @param organizer The new UserImpl object representing the organizer.
     */
    public void updateOrganizer(UserImpl organizer) {
        event.setOrganizer(organizer);
    }

    /**
     * Retrieves the current organizer for the event.
     *
     * @return The UserImpl object representing the organizer.
     */
    public UserImpl getOrganizer() {
        return event.getOrganizer();
    }

    /**
     * Saves or updates the event's state to a database or service.
     * This is a placeholder for actual persistence logic.
     */
    public void saveEvent() {
        // Placeholder for saving logic
        System.out.println("Event saved with name: " + event.getEventName() +
                ", organizer: " + event.getOrganizer().getName());
    }

    /**
     * Resets the event data, if required.
     */
    public void resetEvent() {
        event.setOrganizer(null);
        event.setWaitList(null);
        event.setEventName(null);
        event.setEventDetails(null);
        System.out.println("Event reset.");
    }

}
