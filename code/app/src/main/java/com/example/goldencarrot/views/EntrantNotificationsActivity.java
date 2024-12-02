package com.example.goldencarrot.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.WaitListController;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.db.WaitListRepository;
import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationAdapter;
import com.example.goldencarrot.data.model.notification.NotificationUtils;
import com.example.goldencarrot.data.model.user.UserImpl;
import com.example.goldencarrot.data.model.user.UserUtils;
import com.example.goldencarrot.data.model.waitlist.WaitList;
import com.example.goldencarrot.controller.RanBackground;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
/**
 * Activity that handles displaying notifications for the entrant user.
 * This activity fetches the user's notifications from the database,
 * displays them in a list, and allows the user to accept or decline notifications.
 * Notifications can also be deleted from the list.
 */
public class EntrantNotificationsActivity extends AppCompatActivity {

    private NotificationRepository notificationRepository;
    private NotificationAdapter adapter;
    private Notification selectedNotification;
    private List<Notification> notifications;
    private WaitListRepository waitListRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notifications_view);

        // Apply RNG Background
        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setBackground(RanBackground.getRandomBackground(this));

        // Initialize the NotificationRepository to fetch notifications from Firestore
        notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());
        waitListRepository = new WaitListRepository();

        // Initialize UI components
        Button backButton = findViewById(R.id.back_button_notifications);
        notifications = new ArrayList<>();

        // Initialize the adapter for notifications
        adapter = new NotificationAdapter(this, notifications);
        ListView listView = findViewById(R.id.notification_list_view);
        listView.setAdapter(adapter);

        // Fetch notifications for the current user
        notificationRepository.getNotificationsByUserId(getDeviceId(this),
                new NotificationRepository.NotificationCallback<List<Notification>>() {
                    @Override
                    public void onSuccess(List<Notification> result) {
                        Toast.makeText(EntrantNotificationsActivity.this, "Got notifications", Toast.LENGTH_SHORT).show();
                        notifications.clear();
                        notifications.addAll(result);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EntrantNotificationsActivity.this, "Error",
                                                                        Toast.LENGTH_SHORT).show();
                    }
                });

        // Handle item clicks in the notification list
        listView.setOnItemClickListener((adapterView, view, index, id) -> {
            selectedNotification = notifications.get(index);
            String notificationId = selectedNotification.getNotificationId();

            if (notificationId == null || notificationId.isEmpty()) {
                Toast.makeText(this, "Notification ID not found",
                                                Toast.LENGTH_SHORT).show();
                return;
            }
            // if chosen by lottery, give option to accept or decline invitation
            if (selectedNotification.getStatus().equals(NotificationUtils.CHOSEN)) {
                // Show dialog with options to accept or decline the notification
                new AlertDialog.Builder(EntrantNotificationsActivity.this)
                        .setTitle("Notification")
                        .setMessage(selectedNotification.getMessage())
                        .setPositiveButton("ACCEPT", (dialog, which) -> {
                            handleNotificationAction(notificationId, index);
                            changeStatusInWaitList(getDeviceId(this), selectedNotification.
                                    getWaitListId(), UserUtils.ACCEPTED_STATUS);
                        })
                        .setNegativeButton("DECLINE", (dialog, which) -> {
                            handleNotificationAction(notificationId, index);
                            changeStatusInWaitList(getDeviceId(this), selectedNotification.
                                    getWaitListId(), UserUtils.DECLINED_STATUS);
                            drawReplacementFromWaitlist();

                        })
                        .show();
            } else {
                handleNotificationAction(notificationId, index);
            }
        });

        // Handle back button click to navigate back to the home view
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantNotificationsActivity.this, EntrantHomeView.class);
            startActivity(intent);
        });
    }

    /**
     * Handles notification actions (accept or decline) by deleting the notification
     * and performing necessary status changes in the waitlist.
     *
     * @param notificationId The ID of the notification being acted upon.
     * @param index The index of the notification in the list.
     */
    private void handleNotificationAction(String notificationId, int index) {
        notificationRepository.deleteNotification(notificationId, new NotificationRepository.NotificationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(EntrantNotificationsActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                notifications.remove(index);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EntrantNotificationsActivity.this, "Error deleting notification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * TODO: Implement logic to change the user's status in the waitlist when a notification is accepted or declined.
     */
    private void changeStatusInWaitList(String deviceId, String waitListId, String status) {
        UserImpl user = new UserImpl();
        user.setUserId(deviceId);
        waitListRepository.updateUserStatusInWaitList(waitListId, user, status);
    }

    /**
     * Retrieves the Android device ID for the current device.
     *
     * @param context The application context.
     * @return The device ID as a string.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void drawReplacementFromWaitlist(){

        // We get the waitlist using the event id to ensure that the waitlist document
        // is associated to an event document
        waitListRepository.getWaitListByEventId(selectedNotification.getEventId(),
                new WaitListRepository.WaitListCallback() {
            @Override
            public void onSuccess(WaitList waitList) {
                WaitListController waitListController = new WaitListController(waitList);

                try {
                    // draw replacement
                    waitListController.selectRandomWinnersAndUpdateStatus(1);

                    waitListRepository.updateWaitListInDatabase(waitListController.getWaitList());

                    Toast.makeText(EntrantNotificationsActivity.this,
                            "Successfully drew a replacement ", Toast.LENGTH_SHORT).show();

                } catch (Exception e){
                    Toast.makeText(EntrantNotificationsActivity.this,
                            "Not enough waiting users to draw " +
                                    "replacement", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EntrantNotificationsActivity.this,
                        "Error fetching the waitlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
