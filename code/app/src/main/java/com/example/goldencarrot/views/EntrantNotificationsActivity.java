package com.example.goldencarrot.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
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
    NotificationRepository notificationRepository;
    NotificationAdapter adapter;
    List<Notification> notifications;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notifications_view);

        NotificationRepository notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());

        Button backButton = findViewById(R.id.back_button_notifications);

        notifications = new ArrayList<>();


        // Initialize adapter
        adapter = new NotificationAdapter(this, notifications);
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

        listView.setOnItemClickListener((adapterView, view, index, id) -> {
            Notification selectedNotification = notifications.get(index);
            String notificationId = selectedNotification.getNotificationId();

            if (notificationId == null || notificationId.isEmpty()) {
                Toast.makeText(this, "Notification ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show dialog
            new AlertDialog.Builder(EntrantNotificationsActivity.this)
                    .setTitle("Notification")
                    .setMessage(selectedNotification.getMessage())
                    .setPositiveButton("ACCEPT", (dialog, which) -> {
                        // Call deleteNotification method
                        notificationRepository.deleteNotification(notificationId, new NotificationRepository.NotificationCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                Toast.makeText(EntrantNotificationsActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                                notifications.remove(index);
                                adapter.notifyDataSetChanged();
                                changeStatusInWaitList();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(EntrantNotificationsActivity.this, "Error deleting notification", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("DECLINE", (dialog, which) -> {
                        notificationRepository.deleteNotification(notificationId, new NotificationRepository.NotificationCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                Toast.makeText(EntrantNotificationsActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                                notifications.remove(index);
                                adapter.notifyDataSetChanged();
                                changeStatusInWaitList();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(EntrantNotificationsActivity.this, "Error deleting notification", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .show();
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

    /**
     * Todo change the user status in the waitlist
     */
    private void changeStatusInWaitList() {
    }

    private String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
