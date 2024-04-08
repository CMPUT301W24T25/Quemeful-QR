package com.android.quemeful_qr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Adapter class for displaying all upcoming events in a RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private static List<EventHelper> events;
    private Context context;
    private static EventClickListenerInterface mClickListener;

    /**
     * Constructor for EventAdapter.
     * @param events The upcoming events.
     * @param mClickListener The listener for event clicks.
     */
    public EventAdapter(Context context, List<EventHelper> events, EventClickListenerInterface mClickListener) {
        this.events = events;
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHelper event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText(event.getFormattedDate());

        if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
            Glide.with(context)
                    .load(event.getPoster()) // Load the image directly from the URI
                    .placeholder(R.drawable.gradient_background) // Optional: a placeholder until the image loads
                    .error(R.drawable.gradient_background) // Optional: an error image if the load fails
                    .into(holder.image);
        } else {
            // Use a default image if no poster URI is present
            holder.image.setImageResource(R.drawable.gradient_background);
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

//    public interface EventClickListenerInterface {
//        void onEventClick(EventHelper event);
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, date;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            image = itemView.findViewById(R.id.admin_event_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("events", "Item clicked");
            if (mClickListener != null) mClickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
}
