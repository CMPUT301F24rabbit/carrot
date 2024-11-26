package com.example.goldencarrot;

import static org.junit.jupiter.api.Assertions.*;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;

import org.junit.jupiter.api.Test;

public class EventTest {
    private Event mockEvent;
    private UserImpl mockOrganizer;

    private UserImpl getMockOrganizer(String email, String name){
        try {
            String userProfileImage = "android.resource://" + getClass().getPackageName() + "/drawable/profilepic1";
            mockOrganizer = new UserImpl(email,
                    "ORGANIZER",
                    name,
                    null,
                    false,
                    false,
                    userProfileImage);
        } catch (Exception e) {

        }
        return mockOrganizer;
    }
    private Event mockEventSetup(UserImpl organizer){
        try {
            mockEvent = new Event(organizer);
        } catch (Exception e) {

        }
        return mockEvent;
    }
    /**
     * Testing getOrganizer function
     */
    @Test
    void testGetOrganizer() {
        UserImpl organizer1 = getMockOrganizer("mock1@gmail.com", "mock1");
        Event mockEvent = mockEventSetup(organizer1);
        assertEquals(organizer1, mockEvent.getOrganizer());

        // change organizer for event
        UserImpl organizer2 = getMockOrganizer("mock2@gmail.com", "mock2");
        mockEvent.setOrganizer(organizer2);
        assertEquals(organizer2, mockEvent.getOrganizer());

        // check if organizer2 is not the same as organizer1
        assertNotEquals(organizer1, mockEvent.getOrganizer());
    }

    /**
     * Testing setters and getters for event name
     */
    @Test
    void testEventName() {
        UserImpl organizer1 = getMockOrganizer("mock1@gmail.com", "mock1");
        Event mockEvent = mockEventSetup(organizer1);

        // set an event name
        mockEvent.setEventName("Fundraiser");
        assertEquals("Fundraiser", mockEvent.getEventName());

        // set a different event name
        mockEvent.setEventName("Bake Sale");
        assertNotEquals("Fundraiser", mockEvent.getEventName());
    }
    /**
     * Testing setters and getters for event details
     */
    @Test
    void testEventDetails() {
        UserImpl organizer1 = getMockOrganizer("mock1@gmail.com", "mock1");
        Event mockEvent = mockEventSetup(organizer1);

        // set event details
        mockEvent.setEventDetails("Wear formal clothes");
        assertEquals("Wear formal clothes", mockEvent.getEventDetails());

        // set different event details
        mockEvent.setEventDetails("Bring a present!");
        assertNotEquals("Wear formal clothes", mockEvent.getEventDetails());
    }

}
