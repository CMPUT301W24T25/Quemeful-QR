package com.android.quemeful_qr;

import android.content.Context;
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

import java.util.List;

/**
 * This is an adapter class used to put the events list organized that day, and
 * all the upcoming events in recyclerview.
 */
public class EventsTodayAdapter extends RecyclerView.Adapter<EventsTodayAdapter.EventViewHolder>{
    private static List<EventHelper> events;
    private Context context;
    private static EventClickListenerInterface mClickListener;

    /**
     * EventsTodayAdapter constructor with parameters.
     * @param context Context
     * @param events All events on that day or upcoming.
     */
    public EventsTodayAdapter(Context context, List<EventHelper> events, EventClickListenerInterface clickListener){
        this.context = context;
        this.events = events;
        this.mClickListener = clickListener;
    }

    /**
     * This method is called by the recyclerview it creates a new view.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return ViewHolder
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.eventstodaycard, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * This method is used to bind the new view with its data (the event attributes).
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventHelper event = events.get(position);
        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());
        holder.eventTime.setText(event.getTime());

        if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
            byte[] decodedString = Base64.decode(event.getPoster().trim(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.eventImage.setImageBitmap(decodedByte);
        } else {
            holder.eventImage.setImageResource(R.drawable.gradient_background); // Placeholder if no image is present
        }
    }

    /**
     * This method is used to pass the size of the dataset to the recyclerview.
     * @return int size (count of events)
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * This event view holder child class is used to create the new view holder for events with its details.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView eventImage;
        TextView eventTitle, eventLocation, eventTime;

        /**
         * Defining a click listener for the ViewHolder's View.
         * @param itemView used to initialize the event attributes.
         */
        public EventViewHolder(View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventTime = itemView.findViewById(R.id.eventTime);
            itemView.setOnClickListener(this);
        }

        /**
         * This method is used to find the event position in the events list, that was clicked.
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            Log.d("EventsTodayAdapter", "Item clicked");
            if (mClickListener != null) mClickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
    
} // adapter class closing
