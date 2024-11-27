package com.example.goldencarrot;

import android.util.Log;

import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.db.UserRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.user.User;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Optional;

public class TestDataHelper {
    private static final String TAG = "TestDataHelper";
    public static final String TEST_USER_ID = "123456789";
    public static final String EVENT_NAME = "SpiderMan Party";
    public static final String TEST_USER_NAME = "SpiderMan";

    final UserRepository userRepository;
    final EventRepository eventRepository;
    final WaitListRepository waitListRepository;
    final NotificationRepository notificationRepository;

    User userTest;
    Event eventTest;
    WaitList waitListTest;
    Notification notificationTest;

    public TestDataHelper() throws Exception {


        userRepository = new UserRepository();
        eventRepository = new EventRepository();
        waitListRepository = new WaitListRepository();
        notificationRepository = new NotificationRepository(
                                                    FirebaseFirestore.getInstance());

        createTestUser();
        createTestEvent();
    }

    private void createTestUser() throws Exception {
        userTest = new UserImpl(
                "spiderMan@yahoo.com",
                UserUtils.PARTICIPANT_TYPE,
                TEST_USER_NAME,
                Optional.of(TEST_USER_ID),
                true,
                true,
                "userProfileImage"
        );

        userRepository.addUser(userTest, TEST_USER_ID);
    }

    private void createTestEvent() {
        UserImpl organizerUser = new UserImpl();
        organizerUser.setUserId("777");

        eventTest = new Event(
                organizerUser,
                EVENT_NAME,
                "UI-TestLocation",
                new Date(),
                "eventDetails",
                1234
        );
        eventTest.setGeolocationEnabled(true);

        Log.d(TAG, "Creating Event");
        // Add event to Firestore
        eventRepository.addEvent(eventTest, 99999, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event updatedEvent) {
                eventTest = updatedEvent; // Update eventTest with the correct eventId
                Log.d(TAG, "Event created successfully: " + eventTest.toString());

                Log.d(TAG, "Fetching created Waitlist for Test Event");
                // Fetch the created WaitList
                waitListRepository.getWaitListByEventId(eventTest.getEventId(), new WaitListRepository.WaitListCallback() {
                    @Override
                    public void onSuccess(WaitList waitList) {
                        waitListTest = waitList;
                        Log.d(TAG, "Fetched Waitlist: " + waitList.toString());

                        // Now create the notification after waitlist is fetched
                        createNotification();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "Could not fetch waitlist");
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Could not create the event");
            }
        });
    }


    private void createNotification() {
         Notification notification = new Notification(
                userTest.getUserId(),
                eventTest.getEventId(),
                waitListTest.getWaitListId(),
                "1234",
                "message",
                "CHOSEN"
        );

        notificationRepository.addNotification(notification, new
                NotificationRepository.NotificationCallback<Notification>() {
            @Override
            public void onSuccess(Notification result) {
                notificationTest = result;
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Could create test notification");
            }
        });
    }

    public void deleteData(){
        userRepository.deleteUser("123456789");

        eventRepository.deleteEvent(eventTest.getEventId());

        waitListRepository.deleteWaitList(waitListTest.getWaitListId());

        notificationRepository.deleteNotification(notificationTest.getNotificationId(),
                new NotificationRepository.NotificationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.d(TAG, "Notification deleted successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error when creation Notification");
            }
        });
    }
}
