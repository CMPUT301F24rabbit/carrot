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
    public static final String CANCELLED = "CANCELLED";
    public static final String SINGLE_USER = "SINGLEUSER";

    // Notification messages
    public static final String NOT_CHOSEN_MESSAGE = "Sorry, you were not chosen. Don't worry, " +
            "if someone cancels, you will get another chance!";
    public static final String CHOSEN_MESSAGE = "You won! You can now accept your invitation to join" +
            "our event!";
    public static final String CANCELLED_MESSAGE = "You have been cancelled from accepting your " +
            "invitation to join this event";
    public static final String SINGLE_USER_MESSAGE = "You are receiving this because you are an" +
            " entrant with notifications on";


    // valid notification status
    public static final List<String> validNotificationStatus = Arrays.asList(CHOSEN, NOT_CHOSEN, WAITING);
}
