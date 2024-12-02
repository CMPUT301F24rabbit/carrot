package com.example.goldencarrot;

import android.util.Log;

import com.example.goldencarrot.data.model.notification.NotificationPermissionRequester;

public class NoNotificationPermissionRequester implements NotificationPermissionRequester {

    @Override
    public void requestNotificationPermission() {
        // Does nothing for testing purposes
        Log.d("NoNotificationPermissionRequester", "Mock: No permission requested.");
    }
}
