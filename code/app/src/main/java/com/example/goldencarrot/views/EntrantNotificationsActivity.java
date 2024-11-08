package com.example.goldencarrot.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.controller.NotificationController;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.model.notification.Notification;
import com.example.goldencarrot.data.model.notification.NotificationAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EntrantNotificationsActivity extends AppCompatActivity {
    NotificationController notificationController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notifications_view);

        NotificationController notificationController = new NotificationController();
        NotificationRepository notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());


        Button addNotificationButton = findViewById(R.id.add_notification);
        Button deleteNotification = findViewById(R.id.delete_notification);
        Button backButton = findViewById(R.id.back_button_notifications);



        // Example list of notifications
        List<Notification> notifications = new ArrayList<>();


        // Initialize adapter
        NotificationAdapter adapter = new NotificationAdapter(this, notifications);
        ListView listView = findViewById(R.id.notification_list_view);
        listView.setAdapter(adapter);

        // get notifications for user
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
                Toast.makeText(EntrantNotificationsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


        addNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Notification notification = new Notification();
                notification.setWaitListId("waitlistid");
                notification.setStatus("status");
                notification.setEventId("eventId");
                notification.setMessage("message");
                notification.setUserId("userId");

                notificationRepository.addNotification(notification, new NotificationRepository.NotificationCallback<Notification>() {
                    @Override
                    public void onSuccess(Notification result) {
                        Toast.makeText(EntrantNotificationsActivity.this, "added notification", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EntrantNotificationsActivity.this, "Notification not added", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });

        deleteNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notificationRepository.deleteNotification("Z24DN9kshjhaPsUwmTZX", new NotificationRepository.NotificationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(EntrantNotificationsActivity.this, "Deleted notification", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntrantNotificationsActivity.this,
                        EntrantHomeView.class);
                startActivity(intent);
            }
        });
    }

    private String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


}
