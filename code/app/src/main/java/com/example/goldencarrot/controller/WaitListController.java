package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaitListController {
    private final WaitList waitList;
    private final WaitListRepository waitListRepository;
    private final Random random;
    private Integer waitlistLimit; // Optional limit for the waitlist

    public WaitListController(WaitList waitList, WaitListRepository waitListRepository, Integer waitlistLimit) {
        this.waitList = waitList;
        this.waitListRepository = waitListRepository;
        this.random = new Random();
        this.waitlistLimit = waitlistLimit; // Initialize with limit if provided
    }

    /**
     * Adds a user to the waitlist if there is space.
     *
     * @param user the user to add
     * @return true if the user was added successfully, false if the waitlist is full
     */
    public boolean addUserToLottery(UserImpl user) {
        // Check if the waitlist is full based on the limit
        if (waitlistLimit != null && waitList.getUserArrayList().size() >= waitlistLimit) {
            System.out.println("Waitlist is full. Cannot add more users.");
            return false;
        }

        boolean added = waitList.addUserToWaitList(user);
        if (added) {
            // Save the updated waitlist to the database
            waitListRepository.addUserToWaitList(waitList.getEventId(), user, new WaitListRepository.FirestoreCallback() {
                @Override
                public void onSuccess(Object result) {
                    System.out.println("User added to waitlist successfully in Firestore.");
                }

                @Override
                public void onFailure(Exception e) {
                    System.err.println("Failed to add user to waitlist in Firestore: " + e.getMessage());
                }
            });
        }
        return added;
    }

    /**
     * Selects a random user from the waitlist and updates their status to "accepted."
     *
     * @return the selected user, or null if the waitlist is empty
     */
    public List<UserImpl> selectRandomWinners(int count) {
        ArrayList<UserImpl> userArrayList = waitList.getUserArrayList();
        List<UserImpl> winners = new ArrayList<>();

        if (userArrayList.isEmpty()) {
            System.out.println("Waitlist is empty. No users to select.");
            return winners;
        }

        while (winners.size() < count && !userArrayList.isEmpty()) {
            int winnerIndex = random.nextInt(userArrayList.size());
            UserImpl winner = userArrayList.remove(winnerIndex);
            winners.add(winner);

            waitListRepository.updateUserStatusInWaitList(waitList.getEventId(), winner, "accepted");
        }

        return winners;
    }

    /**
     * Placeholder method to simulate sending a notification.
     *
     * @param user the user to notify
     * @param message the notification message
     */
    private void sendNotification(UserImpl user, String message) {
        System.out.println("Notification to " + user.getName() + ": " + message);
        // Actual notification logic to be added later
    }
}
