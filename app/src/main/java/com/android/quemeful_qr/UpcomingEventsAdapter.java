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

public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.ViewHolder>{

    private static List<EventHelper> events;
    private Context context;
    private static EventClickListenerInterface mClickListener;

    public UpcomingEventsAdapter(List<EventHelper> events, EventClickListenerInterface clickListener){
        this.events = events;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomingeventscard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHelper event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText((CharSequence) event.getDate());

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.upcomingeventTitle);
            date = itemView.findViewById(R.id.upcomingeventDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("EventsTodayAdapter", "Item clicked");
            if (mClickListener != null) mClickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
}
