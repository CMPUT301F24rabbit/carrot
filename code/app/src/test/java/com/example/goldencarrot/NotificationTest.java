package com.example.goldencarrot;

import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.controller.NotificationController;
import com.example.goldencarrot.data.model.notification.NotificationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationControllerTest {

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController();
    }

    @Test
    void testGetOrCreateNotChosenNotification() {
        String userId = "user123";
        String eventId = "event456";
        String waitListId = "waitList789";

        Notification notification = notificationController.getOrCreateNotChosenNotification(userId, eventId, waitListId);

        assertEquals(userId, notification.getUserId());
        assertEquals(eventId, notification.getEventId());
        assertEquals(waitListId, notification.getWaitListId());
        assertEquals(NotificationUtils.NOT_CHOSEN_MESSAGE, notification.getMessage());
        assertEquals(NotificationUtils.NOT_CHOSEN, notification.getStatus());
    }

    @Test
    void testGetOrCreateChosenNotification() {
        String userId = "userABC";
        String eventId = "eventDEF";
        String waitListId = "waitListGHI";

        Notification notification = notificationController.getOrCreateChosenNotification(userId, eventId, waitListId);

        assertEquals(userId, notification.getUserId());
        assertEquals(eventId, notification.getEventId());
        assertEquals(waitListId, notification.getWaitListId());
        assertEquals(NotificationUtils.CHOSEN_MESSAGE, notification.getMessage());
        assertEquals(NotificationUtils.CHOSEN, notification.getStatus());
    }

    @Test
    void testNotificationConstructor() {
        Notification notification = new Notification("user123", "event456", "waitList789", "notifId001", "Test message", "Pending");

        assertEquals("user123", notification.getUserId());
        assertEquals("event456", notification.getEventId());
        assertEquals("waitList789", notification.getWaitListId());
        assertEquals("notifId001", notification.getNotificationId());
        assertEquals("Test message", notification.getMessage());
        assertEquals("Pending", notification.getStatus());
    }

    @Test
    void testSetAndGetUserId() {
        Notification notification = new Notification();
        notification.setUserId("user123");
        assertEquals("user123", notification.getUserId());
    }

    @Test
    void testSetAndGetEventId() {
        Notification notification = new Notification();
        notification.setEventId("event456");
        assertEquals("event456", notification.getEventId());
    }

    @Test
    void testSetAndGetWaitListId() {
        Notification notification = new Notification();
        notification.setWaitListId("waitList789");
        assertEquals("waitList789", notification.getWaitListId());
    }

    @Test
    void testSetAndGetNotificationId() {
        Notification notification = new Notification();
        notification.setNotificationId("notifId001");
        assertEquals("notifId001", notification.getNotificationId());
    }

    @Test
    void testSetAndGetMessage() {
        Notification notification = new Notification();
        notification.setMessage("Test message");
        assertEquals("Test message", notification.getMessage());
    }

    @Test
    void testSetAndGetStatus() {
        Notification notification = new Notification();
        notification.setStatus("Pending");
        assertEquals("Pending", notification.getStatus());
    }
}

