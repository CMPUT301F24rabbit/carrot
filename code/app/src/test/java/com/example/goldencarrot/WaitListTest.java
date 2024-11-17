package com.example.goldencarrot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class WaitListTest {
    private WaitList mockWaitlist;

    private UserImpl mockUserSetup(String email, String name, String uid) {
        UserImpl mockUser = null;
        try {
            mockUser = new UserImpl(email, "PARTICIPANT", 
                    name, null, 
                    false, 
                    false);
            mockUser.setUserId(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mockUser;
    }

    @BeforeEach
    void setUp() {
        Map<String, String> userMap = new HashMap<>();
        mockWaitlist = new WaitList(3, "mockWaitlistId", 
                "mockEventId", "Mock Event", userMap);
    }

    @Test
    void testWaitlistGetEvent() {
        assertEquals("mockEventId", mockWaitlist.getEventId(), "Event ID " +
                "should match");

        mockWaitlist.setEventId("newEventId");
        assertEquals("newEventId", mockWaitlist.getEventId(), "Event ID should " +
                "update correctly");
    }

    @Test
    void testAddingUserToWaitlistMap() {
        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1", "fakeId1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2", "fakeId2");

        // Add users to the userMap with "waiting" status
        mockWaitlist.getUserMap().put(mockUser1.getUserId(), "waiting");
        mockWaitlist.getUserMap().put(mockUser2.getUserId(), "waiting");

        assertEquals(2, mockWaitlist.getUserMap().size(), "User map should" +
                " contain 2 users");
        assertEquals("waiting", mockWaitlist.getUserMap().get(mockUser1.getUserId()),
                "User1 status should be 'waiting'");
        assertEquals("waiting", mockWaitlist.getUserMap().get(mockUser2.getUserId()),
                "User2 status should be 'waiting'");
    }

    @Test
    void testUpdatingUserStatusInWaitlistMap() {
        UserImpl mockUser = mockUserSetup("user1@gmail.com", "user1", "fakeId1");

        // Add user with "waiting" status
        mockWaitlist.getUserMap().put(mockUser.getUserId(), "waiting");

        // Update user status to "accepted"
        mockWaitlist.getUserMap().put(mockUser.getUserId(), "accepted");

        assertEquals("accepted", mockWaitlist.getUserMap().get(mockUser.getUserId()),
                "User status should update to 'accepted'");
    }

    @Test
    void testRemovingUserFromWaitlistMap() {
        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1", "fakeId1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2", "fakeId2");

        // Add users to the userMap
        mockWaitlist.getUserMap().put(mockUser1.getUserId(), "waiting");
        mockWaitlist.getUserMap().put(mockUser2.getUserId(), "waiting");

        // Remove one user
        mockWaitlist.getUserMap().remove(mockUser1.getUserId());

        assertEquals(1, mockWaitlist.getUserMap().size(), "User map should " +
                "contain 1 user after removal");
        assertFalse(mockWaitlist.getUserMap().containsKey(mockUser1.getUserId()), "User1 " +
                "should no longer be in the user map");
    }

    @Test
    void testIsFull() {
        mockWaitlist.setLimitNumber(2);

        UserImpl mockUser1 = mockUserSetup("user1@gmail.com", "user1", "fakeId1");
        UserImpl mockUser2 = mockUserSetup("user2@gmail.com", "user2", "fakeId2");

        // Add users to the waitlist
        mockWaitlist.getUserMap().put(mockUser1.getUserId(), "waiting");
        mockWaitlist.getUserMap().put(mockUser2.getUserId(), "waiting");

        assertTrue(mockWaitlist.isFull(), "Waitlist should be full when the number of " +
                "users waiting equals the limit");
    }
}
