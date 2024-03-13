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

import com.bumptech.glide.Glide;

import java.util.List;
/**
 * puts upcoming event list in recyclerview
 * recyclerview puts data into rows on the screen
 * recyclerview reuses old rows that disappears off screen when scrolling
 */
public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.ViewHolder>{

    private static List<EventHelper> events;
    private Context context;
    private static EventClickListenerInterface mClickListener;

    /**
     * constructor
     * @param events
     * @param clickListener
     */
    public UpcomingEventsAdapter(List<EventHelper> events, EventClickListenerInterface clickListener){
        this.events = events;
        this.mClickListener = clickListener;
    }

    /**
     * * recyclerview calls this method when it needs to create a new viewholder
     *      * viewholder has not filled in the view's contents yet
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomingeventscard, parent, false);
        return new ViewHolder(view);
    }

    /**
     *  /**
     *      * recycles old rows to replace old data with new data (associates viewholder with data)
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventHelper event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText((CharSequence) event.getDate());

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            Log.d("EventsTodayAdapter", "Item clicked");
            if (mClickListener != null) mClickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
}
