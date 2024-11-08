package com.example.goldencarrot.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.NotificationRepository;
import com.example.goldencarrot.data.model.notification.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

public class EntrantNotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_notifications_view);

        NotificationRepository notificationRepository = new NotificationRepository(FirebaseFirestore.getInstance());


        Button addNotificationButton = findViewById(R.id.add_notification);
        Button deleteNotification = findViewById(R.id.delete_notification);
        Button updateNotification = findViewById(R.id.update_notification);



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

        updateNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
