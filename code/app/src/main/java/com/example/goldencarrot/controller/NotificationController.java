package com.example.goldencarrot.controller;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationUtils;
import com.example.goldencarrot.views.EntrantEventDetailsActivity;
import com.example.goldencarrot.views.EntrantHomeView;
import com.example.goldencarrot.views.EntrantNotificationsActivity;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Notification Controller provides all the methods to update
 * the Notification Model
 */
public class NotificationController{
    Notification notification;
    NotificationRepository notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());
    EventRepository eventRepository = new EventRepository();


    public NotificationController(){
        // Singleton
        this.notification = createNotification();
    }

    /**
     * Creates a new notification model and returns it
     * @return Notification model
     */
    public Notification createNotification(){
        return notification;
    }

    /**
     *  Creates notification for Not chosen entrants
     * @param userId of the user
     * @param eventId related to the notification
     * @param waitListId related to the notification
     * @return notification
     */
    public Notification getOrCreateNotChosenNotification(final String userId,
                                                         final String eventId,
                                                         final String waitListId){
        return  new Notification(userId, eventId, waitListId, null,
                NotificationUtils.NOT_CHOSEN_MESSAGE, NotificationUtils.NOT_CHOSEN);
    }

    /**
     *  Creates notification for chosen entrants
     * @param userId of the user
     * @param eventId related to the notification
     * @param waitListId related to the notification
     * @return notification
     */
    public Notification getOrCreateChosenNotification(final String userId,
                                                         final String eventId,
                                                         final String waitListId){
        return new Notification(userId, eventId, waitListId, null,
                NotificationUtils.CHOSEN_MESSAGE, NotificationUtils.CHOSEN
        );
    }
    public Notification getOrCreateCancelledNotification(final String userId,
                                                        final String eventId,
                                                        final String waitListId) {
        return new Notification(userId, eventId, waitListId, null,
                NotificationUtils.CANCELLED_MESSAGE, NotificationUtils.CANCELLED
        );
    }
    public Notification getNotification(){
        return this.notification;
    }

    /**
     * Creates notification for a single User
     * @param userId of the user to receiving notification
     */
    public Notification getOrCreateNotification(final String userId) {
        return new Notification(userId, null, null, null,
                NotificationUtils.SINGLE_USER_MESSAGE, NotificationUtils.SINGLE_USER);
    }

    public void changeNotificationStatus(final Notification notification, final String status) throws Exception {
        if (!NotificationUtils.validNotificationStatus.contains(status)){
            throw new Exception("Invalid Notification Status");
        }

        notification.setStatus(status);
    }

    /**
     * Builds and displays notification on android system
     * @param messageBody
     * @param messageTitle
     */
    public void sendNotification(String messageBody, String messageTitle, String eventId, String eventName, Context context) {
        Intent intent;
        if (messageTitle.equals(NotificationUtils.SINGLE_USER)) {
            intent = new Intent(context, EntrantHomeView.class);
            messageTitle = "To All Entrants";
        } else {
            intent = new Intent(context, EntrantNotificationsActivity.class);
            intent.putExtra("eventId", eventId);
            messageBody = "Event: " + eventName + ", " + messageBody;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE);


        // build notification with message body and title
        String channelId = context.getString(R.string.notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // build notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationChannel notificationChannel = new NotificationChannel(context.getString(R.string.notification_channel_id),
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("This is the notification channel");
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int notificationId = createID();

        // send notification
        notificationManager.notify(notificationId, notificationBuilder.build());

        // displays notification
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()){
            NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build());
        }

    }

    /**
     * Generates unique notification id using local time (can be changed for something more elegant)
     * @return id the randomly generated notification id
     */
    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }

    /**
     * displays all notifications on android system
     * @param notifications arraylist of notifications
     */
    public void displayNotifications(ArrayList<Notification> notifications, Context context) {
        if (!notifications.isEmpty()) {
            Toast.makeText(context, "You have new notifications!", Toast.LENGTH_SHORT).show();
            for (Notification notification : notifications) {
                if (notification.getEventId() != null) {
                    eventRepository.getBasicEventById(notification.getEventId(), new EventRepository.EventCallback() {
                        @Override
                        public void onSuccess(Event event) {
                            sendNotification(notification.getMessage(), notification.getStatus(), notification.getEventId(), event.getEventName(), context);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            e.getMessage();
                        }
                    });
                } else {
                    sendNotification(notification.getMessage(), notification.getStatus(), null, null, context);
                }
                /*
                notificationRepository.deleteNotification(notification.getNotificationId(),
                        new NotificationRepository.NotificationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Log.d(TAG, "Successfully deleted notification from Firebase");
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "failed to delete notification");
                    }
                });
                 */
            }
        }
    }

}
