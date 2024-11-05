package com.example.goldencarrot.controller;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaitlistController {
    private final WaitList waitList;
    private final WaitListRepository waitListRepository;
    private final Random random;

    public WaitlistController(WaitList waitList, WaitListRepository waitListRepository) {
        this.waitList = waitList;
        this.waitListRepository = waitListRepository;
        this.random = new Random();
    }

    /**
     * Adds a user to the waitlist if there is space.
     *
     * @param user the user to add
     * @return true if the user was added successfully, false if the waitlist is full
     */
    public boolean addUserToLottery(UserImpl user) {
        boolean added = waitList.addUserToWaitList(user);
        if (waitList.isFull()) {
            System.out.println("Waitlist is full. Cannot add more users.");
            return false;
        }
        if (added) {
            // Save the updated waitlist to the database
            waitListRepository.addUserToWaitList(waitList.getEvent().getEventName(), user, new WaitListRepository.FirestoreCallback() {
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

            waitListRepository.updateUserStatusInWaitList(waitList.getEvent().getEventName(), winner, "accepted");
        }

        return winners;
    }


    /**
     * Removes a user from the waitlist.
     *
     * @param user the user to remove
     * @return true if the user was removed successfully, false otherwise
     */
    public boolean removeUserFromLottery(UserImpl user) {
        boolean removed = waitList.getUserArrayList().remove(user);
        if (removed) {
            // Update the waitlist size in Firestore
            waitListRepository.deleteWaitList(waitList.getEvent().getEventName());
            waitListRepository.createWaitList(waitList, waitList.getEvent().getEventName());
        }
        return removed;
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
