package com.example.goldencarrot.data.model.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.goldencarrot.R;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private final Context context;
    private final List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_list, parent, false);
        }

        Notification notification = notifications.get(position);

        // Initialize the views.
        TextView messageView = convertView.findViewById(R.id.notification_message);
        TextView statusView = convertView.findViewById(R.id.notification_event_name);
        TextView eventIdView = convertView.findViewById(R.id.notification_event_id);

        messageView.setText(notification.getMessage());
        statusView.setText(notification.getStatus());
        eventIdView.setText("Event ID: " + notification.getEventId());

        return convertView;
    }
}
