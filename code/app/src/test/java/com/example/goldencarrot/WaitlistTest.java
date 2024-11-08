package com.example.goldencarrot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WaitlistTest {
    private Event mockEvent;
    private UserImpl mockOrganizer;
    private WaitList mockWaitlist;

    private UserImpl mockOrganizerSetup(String email, String name) {
        try {
            mockOrganizer = new UserImpl(email, "ORGANIZER", name, null, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mockOrganizer;
    }

    private UserImpl mockUserSetup(String email, String name) {
        UserImpl mockUser = null;
        try {
            mockUser = new UserImpl(email, "PARTICIPANT", name, null, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mockUser;
    }

    private Event mockEventSetup(UserImpl organizer) {
        try {
            mockEvent = new Event(organizer);
            mockEvent.setEventId("mockEventId");
            mockEvent.setWaitListId("mockWaitlistId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mockEvent;
    }

    private WaitList mockWaitlistSetup(Event event) {
        ArrayList<UserImpl> mockUserList = new ArrayList<>();
        mockWaitlist = new WaitList(0, "mockWaitlistId", "mockEventId", mockUserList);
        return mockWaitlist;
    }

    @BeforeEach
    void setUp() {
        mockOrganizer = mockOrganizerSetup("organizer@example.com", "Organizer");
        mockEvent = mockEventSetup(mockOrganizer);
        mockWaitlist = mockWaitlistSetup(mockEvent);
    }

    @Test
    void testWaitlistGetEvent() {
        assertSame(mockEvent.getEventId(), mockWaitlist.getEventId(), "Event ID should match");

        mockWaitlist.setEventId("noteventId");
        assertNotSame(mockEvent.getEventId(), mockWaitlist.getEventId(), "Event IDs should not match after setting a new one");
    }

    @Test
    void testAddingUserToWaitlist() {
        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2");
        UserImpl mockUser3 = mockUserSetup("user3@gmail.com", "user3");

        mockWaitlist.setLimitNumber(2);
        assertTrue(mockWaitlist.addUserToWaitList(mockUser1), "First user should be added");
        assertTrue(mockWaitlist.addUserToWaitList(mockUser2), "Second user should be added");

        assertEquals(2, mockWaitlist.getUserArrayList().size(), "Waitlist size should be 2 after adding two users");

        assertFalse(mockWaitlist.addUserToWaitList(mockUser3), "Third user should not be added due to limit");
    }

    @Test
    void testDeletingUserFromWaitlist() {
        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2");

        mockWaitlist.setLimitNumber(2);
        assertTrue(mockWaitlist.addUserToWaitList(mockUser1), "First user should be added");
        assertTrue(mockWaitlist.addUserToWaitList(mockUser2), "Second user should be added");

        mockWaitlist.removeUserFromWaitList(mockUser1);
        assertEquals(1, mockWaitlist.getUserArrayList().size(), "Waitlist size should be 1 after removing a user");
    }
}

