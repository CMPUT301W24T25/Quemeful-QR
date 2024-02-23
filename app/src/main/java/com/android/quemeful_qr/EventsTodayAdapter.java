package com.android.quemeful_qr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class EventsTodayAdapter extends RecyclerView.Adapter<EventsTodayAdapter.EventViewHolder>{

    private static List<EventHelper> events;
    private Context context;
    private LayoutInflater mInflater;
    private static EventClickListenerInterface mClickListener;

    public EventsTodayAdapter(Context context, List<EventHelper> events, EventClickListenerInterface clickListener){
        this.context = context;
        this.events = events;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.eventstodaycard, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventHelper event = events.get(position);
        holder.eventTitle.setText(event.getTitle());
        holder.eventLocation.setText(event.getLocation());
        holder.eventTime.setText(event.getTime());
//        holder.eventImage.setImageResource(event.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView eventImage;
        TextView eventTitle, eventLocation, eventTime;

        public EventViewHolder(View itemView) {
            super(itemView);
//            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventTime = itemView.findViewById(R.id.eventTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("EventsTodayAdapter", "Item clicked");
            if (mClickListener != null) mClickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
}
