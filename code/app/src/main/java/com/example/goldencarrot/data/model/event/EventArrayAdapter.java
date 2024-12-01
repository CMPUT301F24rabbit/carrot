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

import com.bumptech.glide.Glide;

/**
 * Adapter to populate a ListView or GridView with event data, including dynamic poster loading.
 */
public class EventArrayAdapter extends android.widget.ArrayAdapter<Event> {

    private ArrayList<Event> eventList;
    private Context context;

    /**
     * Constructor to initialize the adapter with a list of events.
     *
     * @param context the context in which the adapter is used.
     * @param events the list of Event objects to display.
     */
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.eventList = events;
        this.context = context;
    }

    /**
     * Provides a view for a specific item in the list.
     *
     * @param position the position of the item in the list.
     * @param convertView a recycled view to reuse, if available.
     * @param parent the parent view that this item will be attached to.
     * @return a View object representing a single item in the list.
     */
    @NonNull
    @Override
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
            // Set text fields
            eventName.setText(event.getEventName());
            eventLocation.setText(event.getLocation());

            // Format the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = event.getDate() != null ? dateFormat.format(event.getDate()) : "Date not set";
            eventDate.setText(formattedDate);

            eventDetails.setText(event.getEventDetails());

            // Load event poster dynamically using Glide
            String posterUrl = event.getPosterUrl(); // Fetch poster URL
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(context)
                        .load(posterUrl)
                        .placeholder(R.drawable.poster_placeholder) // Placeholder while loading
                        .error(R.drawable.poster_error) // Error image if loading fails
                        .into(eventImage);
            }
        }

        return view;
    }
}

