package com.example.goldencarrot.data.model.notification;

import java.util.Arrays;
import java.util.List;

/**
 * This class provides common global variables for Notification related classes
 */
public class NotificationUtils {

    // notification types
    public static final String CHOSEN = "CHOSEN";
    public static final String NOT_CHOSEN = "NOTCHOSEN";
    public static final String WAITING = "WAITING";
    public static final String SINGLE_USER = "SINGLEUSER";

    // Notification messages
    public static final String NOT_CHOSEN_MESSAGE = "Sorry, you were not chosen. Don't worry, " +
            "if someone cancels, you will get another chance!";
    public static final String CHOSEN_MESSAGE = "You won! Hope to see you there :)";
    public static final String SINGLE_USER_MESSAGE = "You are recieving this because you are an" +
            " entrant with notifications on";

    // valid notification status
    public static final List<String> validNotificationStatus = Arrays.asList(CHOSEN, NOT_CHOSEN, WAITING);
}
