package com.android.quemeful_qr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Map;

/**
 * puts administrator view event list in recyclerview
 * recyclerview puts data into rows on the screen
 * recyclerview reuses old rows that disappears off screen when scrolling
 */
public class admin_event_adapter extends RecyclerView.Adapter<admin_event_adapter.ViewHolder> {
    private Context context;
    private List<Map<String, Object>> events;

    /**
     * constructor
     * @param context
     * @param events
     */
    public admin_event_adapter(Context context, List<Map<String, Object>> events) {
        this.context = context;
        this.events = events;
    }

    /**
     * recyclerview calls this method when it needs to create a new viewholder
     * viewholder has not filled in the view's contents yet
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_event_card, parent, false);
        return new ViewHolder(view);
    }

    /**
     * recycles old rows to replace old data with new data (associates viewholder with data)
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> event = events.get(position);
        holder.bindEventData(event);
    }

    /**
     * recyclerview calls this to get size of dataset
     * @return int
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /** provides a reference to the type of views that are being used
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eventNameTextView;
        private TextView eventDateTextView;
        private ImageView eventImageView;

        /**
         * // Define click listener for the ViewHolder's View
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_title);
            eventDateTextView = itemView.findViewById(R.id.event_date);
            eventImageView = itemView.findViewById(R.id.admin_event_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map<String, Object> event = events.get(position);
                    Intent intent = new Intent(context, Admin_Event_Detail_Activity.class);
                    // Ensure you use the same key ("eventId") as expected in Admin_Event_Detail_Activity
                    intent.putExtra("eventId", (String) event.get("eventId"));
                    context.startActivity(intent);
                }
            });
        }

        /**
         * load image from URL to the event imageview in the recyclerview of the app
         * @param event
         */
        public void bindEventData(Map<String, Object> event) {
            eventNameTextView.setText((String) event.get("title"));
            eventDateTextView.setText((String) event.get("date"));

            String imageUrl = (String) event.get("eventImage");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext()).load(imageUrl).into(eventImageView);
            } else {

            }
        }
    }
}
