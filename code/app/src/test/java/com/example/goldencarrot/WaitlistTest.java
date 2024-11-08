package com.example.goldencarrot;

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

        // test waitlist event
        assertSame(mockEvent.getEventId(), mockWaitlist.getEventId());

        mockWaitlist.setEventId("noteventId");
        assertNotSame(mockEvent.getEventId(), mockWaitlist.getEventId());
    }
}
