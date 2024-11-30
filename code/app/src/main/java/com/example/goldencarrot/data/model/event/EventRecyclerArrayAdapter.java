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

import com.bumptech.glide.Glide; // Glide for image loading
import com.example.goldencarrot.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView.Adapter for displaying a list of Events with poster functionality.
 */
public class EventRecyclerArrayAdapter extends RecyclerView.Adapter<EventRecyclerArrayAdapter.EventViewHolder> {

    /**
     * Interface to handle long-click events on events.
     */
    public interface OnEventLongClickListener {
        void onEventLongClick(Event event);
    }

    private final Context context;
    private final List<Event> eventList;
    private final OnEventLongClickListener longClickListener;

    /**
     * Constructor to initialize the adapter.
     *
     * @param context the context in which the adapter is used.
     * @param eventList the list of Event objects to display.
     * @param longClickListener listener for long-click events.
     */
    public EventRecyclerArrayAdapter(Context context, List<Event> eventList, OnEventLongClickListener longClickListener) {
        this.context = context;
        this.eventList = eventList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        Log.d("EventRecyclerArrayAdapter", "Binding event: " + event.getEventName());

        // Set text for each view
        holder.eventName.setText(event.getEventName());
        holder.eventLocation.setText(event.getLocation());
        holder.eventDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(event.getDate()));
        holder.eventDetails.setText(event.getEventDetails());

        // Load event poster using Glide
        String posterUrl = event.getPosterUrl(); // New method to fetch poster URL
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(context)
                    .load(posterUrl)
                    .placeholder(R.drawable.poster_placeholder) // Default placeholder
                    .error(R.drawable.poster_error) // Error image
                    .into(holder.eventImageView);
        } else {
            // Fallback to a default image if no URL is available
            holder.eventImageView.setImageResource(R.drawable.poster_placeholder);
        }

        // Set long-click listener
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onEventLongClick(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder for event items.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventName;
        TextView eventLocation;
        TextView eventDate;
        TextView eventDetails;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.eventImageView);
            eventName = itemView.findViewById(R.id.eventNameView);
            eventLocation = itemView.findViewById(R.id.eventLocationView);
            eventDate = itemView.findViewById(R.id.eventDateView);
            eventDetails = itemView.findViewById(R.id.eventDetailsView);
        }
    }
}
