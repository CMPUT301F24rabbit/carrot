package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.data.model.waitlist.WaitListConfigurator;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Provides methods to edit a WaitList Model and make changes in firebase.
 * This controller method can addUserToLottery, and selectRandomWinners
 */
public class WaitListController {
    private WaitListConfigurator waitList;

    public WaitListController(WaitListConfigurator waitList){
        this.waitList = waitList;
    }

    /**
     * Selects a random list of N winners from the userMap with "waiting" status
     * and updates their status to "accepted".
     *
     * @param count    the number of winners to select
     */
    public void selectRandomWinnersAndUpdateStatus(int count) throws Exception {
        // Retrieve the user map
        Map<String, String> userMap = this.waitList.getUserMap(); // Assuming userMap is in WaitList

        // Filter users with "waiting" status
        List<String> waitingUserIds = userMap.entrySet().stream()
                .filter(entry -> UserUtils.WAITING_STATUS.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Validate if there are enough users with "waiting" status
        if (waitingUserIds.size() < count) {
            throw new Exception("Not enough users with 'waiting' status to sample.");
        }

        // Randomly select winners and update their status
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int winnerIndex = random.nextInt(waitingUserIds.size());
            String winnerId = waitingUserIds.remove(winnerIndex);

            // Update the user's status in the map
            userMap.put(winnerId, UserUtils.ACCEPTED_STATUS);

            // Waitlist document is not updated in this method for testing purposes
        }
    }

    public WaitListConfigurator getWaitList() {
        return waitList;
    }

    public void setWaitList(WaitListConfigurator waitList) {
        this.waitList = waitList;
    }
}