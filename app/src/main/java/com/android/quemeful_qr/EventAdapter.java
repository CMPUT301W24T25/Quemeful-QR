package com.android.quemeful_qr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for displaying all upcoming events in a RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private static List<EventHelper> events;
    private Context context;
    private EventClickListenerInterface mClickListener;

    /**
     * Constructor for EventAdapter.
     * @param events The upcoming events.
     * @param clickListener The listener for event clicks.
     */
    public EventAdapter(List<EventHelper> events, EventClickListenerInterface clickListener) {
        this.events = events;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHelper event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText(event.getDate()); // Make sure this method exists in EventHelper.

        if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
            byte[] decodedString = Base64.decode(event.getPoster().trim(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedByte);
        } else {
            holder.image.setImageResource(R.drawable.gradient_background); // Placeholder if no image is present
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<EventHelper> newEvents) {
        events = newEvents;
        notifyDataSetChanged();
    }

    public interface EventClickListenerInterface {
        void onEventClick(EventHelper event);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, date;
        ImageView image;
        EventClickListenerInterface clickListener;

        public ViewHolder(@NonNull View itemView, EventClickListenerInterface clickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            image = itemView.findViewById(R.id.admin_event_image);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
}
