package com.example.goldencarrot.data.model.event;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goldencarrot.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView.Adapter for displaying a list of Events.
 * This adapter binds event data to each item view in the RecyclerView and handles long-click events.
 */
public class EventRecyclerArrayAdapter extends RecyclerView.Adapter<EventRecyclerArrayAdapter.EventViewHolder> {

    /**
     * Interface to handle long-click events on events.
     */
    public interface OnEventLongClickListener {
        /**
         * Called when an event item is long-clicked.
         *
         * @param event the event that was long-clicked.
         */
        void onEventLongClick(Event event);
    }

    private final Context context;
    private final List<Event> eventList;
    private final OnEventLongClickListener longClickListener;

    /**
     * Constructor to initialize the adapter with context, event list, and long-click listener.
     *
     * @param context the context in which the adapter is used, typically an Activity or Fragment.
     * @param eventList the list of Event objects to be displayed.
     * @param longClickListener listener to handle long-click events on event items.
     */
    public EventRecyclerArrayAdapter(Context context, List<Event> eventList, OnEventLongClickListener longClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.longClickListener = longClickListener;
    }

    /**
     * Creates a new ViewHolder by inflating the event list item layout.
     *
     * @param parent the parent view that the new view will be attached to.
     * @param viewType the type of view to be created (not used in this case).
     * @return a new EventViewHolder instance.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the event data to the views in the ViewHolder.
     *
     * @param holder the ViewHolder to bind the data to.
     * @param position the position of the event in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        Log.d("EventRecyclerArrayAdapter", "clicked on: " + event.getEventName());

        // Set text for each view
        holder.eventName.setText(event.getEventName());
        holder.eventLocation.setText(event.getLocation());
        holder.eventImageView.setImageResource(event.getImageResId());

        // Format and set the event date and details
        holder.eventDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(event.getDate()));
        holder.eventDetails.setText(event.getEventDetails());

        // Set long-click listener
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onEventLongClick(event);
            return true;
        });
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return the size of the event list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for holding the views of each event item in the list.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventName;
        TextView eventLocation;
        TextView eventDate;     // New TextView for Date
        TextView eventDetails;  // New TextView for Details

        /**
         * Constructor to initialize the views for an individual event item.
         *
         * @param itemView the view of a single event item.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.eventImageView);
            eventName = itemView.findViewById(R.id.eventNameView);
            eventLocation = itemView.findViewById(R.id.eventLocationView);
            eventDate = itemView.findViewById(R.id.eventDateView);       // Initialize new Date view
            eventDetails = itemView.findViewById(R.id.eventDetailsView); // Initialize new Details view
        }
    }
}



