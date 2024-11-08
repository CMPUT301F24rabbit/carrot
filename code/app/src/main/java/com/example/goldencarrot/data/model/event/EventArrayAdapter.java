package com.example.goldencarrot.data.model.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.goldencarrot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Array Adapter for Events
 */

public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> eventList;
    private Context context;

    /**
     * Constructor to initialize the adapter with a list of events.
     *
     * @param context the context in which the adapter is used, typically an Activity or Fragment.
     * @param events the list of Event objects to be displayed.
     */
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.eventList = events;
        this.context = context;
    }

    /**
     * Gets a view for a specific item in the list.
     * This method is used to populate a single list item view with event data.
     *
     * @param position the position of the item within the list.
     * @param convertView a recycled view to reuse if available (can be null).
     * @param parent the parent view that this item will be attached to.
     * @return a View object representing a single item in the list.
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        } else {
            view = convertView;
        }

        // Get the event at the current position
        Event event = getItem(position);

        // Populate views with event data
        ImageView eventImage = view.findViewById(R.id.eventImageView);
        TextView eventName = view.findViewById(R.id.eventNameView);
        TextView eventLocation = view.findViewById(R.id.eventLocationView);
        TextView eventDate = view.findViewById(R.id.eventDateView);
        TextView eventDetails = view.findViewById(R.id.eventDetailsView);

        // Check if event is null
        if (event != null) {
            eventName.setText(event.getEventName());
            eventLocation.setText(event.getLocation());

            // Formate the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = event.getDate() != null ? dateFormat.format(event.getDate()) : "Date not set";
            eventDate.setText(formattedDate);

            eventDetails.setText(event.getEventDetails());

            // Static image for DEMO PURPOSESSSSSS
            eventImage.setImageResource(R.drawable.movie);
        }

        return view;
    }
}
