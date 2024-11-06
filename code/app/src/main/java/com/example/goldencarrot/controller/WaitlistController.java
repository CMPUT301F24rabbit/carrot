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

        // Number of attendees to register for the event (to be set by the organizer)
        private int sampleSize = 0;

        public WaitlistController(WaitList waitList, WaitListRepository waitListRepository) {
            this.waitList = waitList;
            this.waitListRepository = waitListRepository;
            this.random = new Random();
        }

        // Method to set the sample size (number of users to register)
        public void setSampleSize(int sampleSize) {
            this.sampleSize = sampleSize;
            System.out.println("Sample size for attendees set to " + sampleSize);
        }



        // Register the user and move them from waitlist to registered list

        // Helper method to randomly select winners (i.e., users to be registered)
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

        // Helper method to simulate sending a notification to users
        private void sendNotification(UserImpl user, String message) {
            System.out.println("Notification to " + user.getName() + ": " + message);
        }

        // Simulate user accepting or declining the invite (randomly for now)
        private boolean inviteUser(UserImpl user) {
            sendNotification(user, "You have been invited to participate!");
            boolean accepted = random.nextBoolean(); // Simulate random acceptance
            if (accepted) {
                waitListRepository.updateUserStatusInWaitList(waitList.getEvent().getEventName(), user, "accepted");
            } else {
                waitListRepository.updateUserStatusInWaitList(waitList.getEvent().getEventName(), user, "declined");
            }
            return accepted;
        }

        // Helper method to invite a replacement user if someone declines
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
    }
