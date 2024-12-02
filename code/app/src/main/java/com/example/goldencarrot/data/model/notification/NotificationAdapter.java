package com.example.goldencarrot.data.model.notification;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.db.EventRepository;
import com.example.goldencarrot.data.model.event.Event;

import java.util.List;

/**
 * Adapter class for displaying notifications in a ListView.
 * This class is responsible for binding data from a list of Notification objects to the views in a list item.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

    private final Context context;
    private final List<Notification> notifications;
    private EventRepository eventRepository;

    /**
     * Constructor of the NotificationAdapter for lists
     *
     * @param context the context provided by the view
     * @param notifications the list of Notification objects to be displayed
     */
    public NotificationAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    /**
     * Creates and returns a view for a specific position in the list.
     * This method inflates the layout for each notification in the array of notifiactions
     *
     * @param position the position of the item within the list.
     * @param convertView convertView
     * @param parent the parent view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        eventRepository = new EventRepository();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_list, parent, false);
        }

        // Get the notification at the specified position.
        Notification notification = notifications.get(position);

        TextView messageView = convertView.findViewById(R.id.notification_message);
        TextView statusView = convertView.findViewById(R.id.notification_event_name);
        TextView eventIdView = convertView.findViewById(R.id.notification_event_id);
        messageView.setText(notification.getMessage());
        statusView.setText(notification.getStatus());

        // get event name
        if (notification.getEventId() != null) {
            eventRepository.getBasicEventById(notification.getEventId(), new EventRepository.EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    eventIdView.setText(event.getEventName());
                    Log.d("NotificationAdapter.java", "Event found");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("NotificationAdapter.java", "Event Not found" + e);
                    eventIdView.setText("Event Name not found");
                }
            });
        } else {
            eventIdView.setText("Join My Event!");
        }

        // Return the completed view for this list item.
        return convertView;
    }
}
