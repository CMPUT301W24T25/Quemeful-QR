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
 * This adapter class is used to put all administrator's view events list in recyclerview.
 */
public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {
    private Context context;
    private List<Map<String, Object>> events;

    /**
     * This is a constructor with parameters.
     * @param context Context of this class.
     * @param events list/array of events.
     */
    public AdminEventAdapter(Context context, List<Map<String, Object>> events) {
        this.context = context;
        this.events = events;
    }

    /**
     * This onCreateViewHolder() is used to create admin event view.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return the admin event view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_event_card, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method binds the admin event view with their respective event data.
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
     * This method is used to count the events.
     * @return int size (count of events)
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * This class is used to create the administrator event view with the event details.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eventNameTextView;
        private TextView eventDateTextView;
        private ImageView eventImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_title);
            eventDateTextView = itemView.findViewById(R.id.event_date);
            eventImageView = itemView.findViewById(R.id.admin_event_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map<String, Object> event = events.get(position);
                    Intent intent = new Intent(context, AdminEventDetailsActivity.class);
                    // Ensure you use the same key ("eventId") as expected in Admin_Event_Detail_Activity
                    intent.putExtra("eventId", (String) event.get("eventId"));
                    context.startActivity(intent);
                }
            });
        }

        /**
         * This method is used to bind the event data including its poster onto the attributes respective fields.
         * @param event the event clicked on by admin.
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
    } // ViewHolder class closing
} // AdminEventAdapter class closing
