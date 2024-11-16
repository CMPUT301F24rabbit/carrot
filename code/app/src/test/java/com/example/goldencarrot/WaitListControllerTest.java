package com.example.goldencarrot;

import com.example.goldencarrot.controller.WaitListController;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WaitListControllerTest {
    private WaitList waitList;
    private WaitListController waitListController;

    @BeforeEach
    void setUp() {
        // Mock a WaitList with a user map
        Map<String, String> userMap = new HashMap<>();
        userMap.put("user1", "waiting");
        userMap.put("user2", "waiting");
        userMap.put("user3", "accepted");
        userMap.put("user4", "waiting");

        waitList = new WaitList(10, "mockWaitlistId", "mockEventId", "Mock Event", userMap);
        waitListController = new WaitListController(waitList);
    }

    @Test
    void testSelectRandomWinnersAndUpdateStatusSuccess() throws Exception {
        int numberOfWinners = 2;

        // Call the method to select winners
        waitListController.selectRandomWinnersAndUpdateStatus(numberOfWinners);

        // Count the number of users with "accepted" status
        long acceptedCount = waitList.getUserMap().values().stream()
                .filter(status -> "accepted".equals(status))
                .count();

        // Assert the number of "accepted" users is as expected
        assertEquals(3, acceptedCount, "There should be 3 accepted users in total after selecting 2 winners.");
    }

    @Test
    void testSelectRandomWinnersAndUpdateStatusNotEnoughWaitingUsers() {
        // Try to select more winners than available waiting users
        int numberOfWinners = 5;

        // Expect an exception to be thrown
        Exception exception = assertThrows(Exception.class, () -> {
            waitListController.selectRandomWinnersAndUpdateStatus(numberOfWinners);
        });

        // Assert the exception message is as expected
        assertEquals("Not enough users with 'waiting' status to sample.", exception.getMessage());
    }
}
