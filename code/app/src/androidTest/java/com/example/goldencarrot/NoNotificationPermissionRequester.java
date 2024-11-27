package com.example.goldencarrot;

import com.example.goldencarrot.data.model.notification.NotificationPermissionRequester;

public class NoNotificationPermissionRequester implements NotificationPermissionRequester {

    @Override
    public void requestNotificationPermission() {
        // Does not make the request for testing purposes
    }
}
