package com.example.goldencarrot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class WaitlistTest {
    private Event mockEvent;
    private UserImpl mockOrganizer;
    private WaitList mockWaitlist;

    private UserImpl mockOrganizerSetup(String email, String name){
        try {
            mockOrganizer = new UserImpl(email,
                    "ORGANIZER",
                    name,
                    null,
                    false,
                    false);
        } catch (Exception e) {

        }
        return mockOrganizer;
    }
    private UserImpl mockUserSetup(String email, String name){
        try {
            mockOrganizer = new UserImpl(email,
                    "PARTICIPANT",
                    name,
                    null,
                    false,
                    false);
        } catch (Exception e) {

        }
        return mockOrganizer;
    }
    private Event mockEventSetup(UserImpl organizer){
        try {
            mockEvent = new Event(organizer);
            mockEvent.setEventId("mockEventId");
            mockEvent.setWaitListId("mockWaitlistId");
        } catch (Exception e) {

        }
        return mockEvent;
    }
    private WaitList mockWaitlistSetup(Event event) {
        ArrayList<UserImpl> mockUserList = new ArrayList<>();
        mockWaitlist = new WaitList(0,
                "mockWaitlistId",
                "mockEventId",
                mockUserList);
        return mockWaitlist;
    }
    @Test
    void testWaitlistGetEvent() {
        // initialize mock waitlist
        UserImpl mockOrganizer = mockOrganizerSetup("mockEmail", "mockName");
        Event mockEvent = mockEventSetup(mockOrganizer);
        WaitList mockWaitlist = mockWaitlistSetup(mockEvent);

        // test waitlist getting event id
        assertSame(mockEvent.getEventId(), mockWaitlist.getEventId());

        // test waitlist event id not matching
        mockWaitlist.setEventId("noteventId");
        assertNotSame(mockEvent.getEventId(), mockWaitlist.getEventId());
    }

    @Test
    void testAddingUserToWaitlist() {
        // initialize mock waitlist
        UserImpl mockOrganizer = mockOrganizerSetup("mockEmail", "mockName");
        Event mockEvent = mockEventSetup(mockOrganizer);
        WaitList mockWaitlist = mockWaitlistSetup(mockEvent);

        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2");
        UserImpl mockUser3 = mockUserSetup("user3@gmail.com", "user3");

        // adding users to waitlist
        mockWaitlist.setLimitNumber(2);
        assertTrue(mockWaitlist.addUserToWaitList(mockUser1));
        assertTrue(mockWaitlist.addUserToWaitList(mockUser2));

        // testing that users were added
        assertEquals(2, mockWaitlist.getUserArrayList().size());

        // testing adding users beyond limit
        assertFalse(mockWaitlist.addUserToWaitList(mockUser3));
    }

    @Test
    void testDeletingUserFromWaitlist() {
        // initialize mock waitlist
        UserImpl mockOrganizer = mockOrganizerSetup("mockEmail", "mockName");
        Event mockEvent = mockEventSetup(mockOrganizer);
        WaitList mockWaitlist = mockWaitlistSetup(mockEvent);

        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2");

        // adding users to waitlist
        mockWaitlist.setLimitNumber(2);
        assertTrue(mockWaitlist.addUserToWaitList(mockUser1));
        assertTrue(mockWaitlist.addUserToWaitList(mockUser2));

        // remove user from waitlist
        mockWaitlist.removeUserFromWaitList(mockUser1);
        assertEquals(1, mockWaitlist.getUserArrayList().size());
    }
        // test waitlist event
        assertSame(mockEvent.getEventId(), mockWaitlist.getEventId());

        mockWaitlist.setEventId("noteventId");
        assertNotSame(mockEvent.getEventId(), mockWaitlist.getEventId());
    }
}
