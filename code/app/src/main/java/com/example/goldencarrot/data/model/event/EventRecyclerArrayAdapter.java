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

public class EventRecyclerArrayAdapter extends RecyclerView.Adapter<EventRecyclerArrayAdapter.EventViewHolder> {

    public interface OnEventLongClickListener{
        void onEventLongClick(Event event);
    }

    private final Context context;
    private final List<Event> eventList;
    private final OnEventLongClickListener longClickListener;

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
        Log.d("EventRecyclerArrayAdapter", "clicked on: " + event.getEventName());
        holder.eventName.setText(event.getEventName());
        holder.eventLocation.setText(event.getLocation());
        holder.eventImageView.setImageResource(event.getImageResId());

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onEventLongClick(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventName;
        TextView eventLocation;

        public EventViewHolder(@NonNull View itemView){
        super(itemView);
        eventImageView = itemView.findViewById(R.id.eventImageView);
        eventName = itemView.findViewById(R.id.eventNameView);
        eventLocation = itemView.findViewById(R.id.eventLocationView);
    }
    }
}



