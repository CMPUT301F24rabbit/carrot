package com.example.goldencarrot.data.model.event;

import android.content.Context;
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

public class EventRecyclerArrayAdapter extends RecyclerView.Adapter<EventRecyclerArrayAdapter.EventViewHolder> {

    private final Context context;
    private final List<Event> eventList;

    public EventRecyclerArrayAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
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
        holder.eventName.setText(event.getEventName());
        holder.eventLocation.setText(event.getLocation());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = event.getDate() != null ? dateFormat.format(event.getDate()) : "Date not set";
        holder.eventDate.setText(formattedDate);
        holder.eventDetails.setText(event.getEventDetails());

        // Set image resource if Event class has an image field
        if (event.getImageResId() != 0) {
            holder.eventImageView.setImageResource(event.getImageResId());
        } else {
            holder.eventImageView.setImageResource(R.drawable.movie); // Default image placeholder
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventName, eventLocation, eventDate, eventDetails;

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



