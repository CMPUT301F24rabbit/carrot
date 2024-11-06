package com.example.goldencarrot.controller;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.waitlist.WaitList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
     * Invites a limited number of random users and handles their responses.
     * If a user declines, another user is randomly selected until the quota is filled or the waitlist is exhausted.
     *
     * @param count the number of users to invite
     */
    public void inviteUsersToLottery(int count) {
        List<UserImpl> selectedUsers = selectRandomWinners(count);

        for (UserImpl user : selectedUsers) {
            boolean accepted = inviteUser(user);
            if (accepted) {
                System.out.println(user.getName() + " accepted the invite.");
            } else {
                System.out.println(user.getName() + " declined the invite. Selecting another user...");
                inviteReplacementUser();
            }
        }
    }

    /**
     * Helper method to invite a single user and simulate response.
     *
     * @param user the user to invite
     * @return true if the user accepts the invite, false if declined
     */
    private boolean inviteUser(UserImpl user) {
        sendNotification(user, "You have been invited to participate!");
        // Simulate user response with a random outcome
        boolean accepted = random.nextBoolean(); // Or use an actual response mechanism
        if (accepted) {
            waitListRepository.updateUserStatusInWaitList(waitList.getEvent().getEventName(), user, "accepted");
        } else {
            waitListRepository.updateUserStatusInWaitList(waitList.getEvent().getEventName(), user, "declined");
        }
        return accepted;
    }

    /**
     * Re-selects a user from the waitlist if a previously invited user declines.
     */
    private void inviteReplacementUser() {
        if (waitList.getUserArrayList().isEmpty()) {
            System.out.println("No more users available in waitlist.");
            return;
        }
        UserImpl replacement = waitList.getUserArrayList().remove(random.nextInt(waitList.getUserArrayList().size()));
        boolean accepted = inviteUser(replacement);
        if (!accepted) {
            inviteReplacementUser(); // Repeat if declined
        }
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

            waitListRepository.updateUserStatusInWaitList(waitList.getEvent().getEventName(), winner, "pending");
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

    /**
     * Retrieves and displays the list of entrants who joined the waiting list for the event.
     */
    public void viewEntrantsInWaitlist() {
        // Fetch the list of users in the waitlist from Firestore
        waitListRepository.getUsersWithStatus(waitList.getEvent().getEventName(), "waiting", new WaitListRepository.FirestoreCallback() {
            @Override
            public void onSuccess(Object result) {
                List<String> entrants = (List<String>) result;
                System.out.println("Entrants in the waiting list:");
                for (String entrant : entrants) {
                    System.out.println(entrant);
                }
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("Error fetching entrants in the waitlist: " + e.getMessage());
            }
        });
    }
}
