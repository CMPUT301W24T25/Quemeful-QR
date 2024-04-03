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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * This is an adapter class used to put all upcoming events in a recycler view.
 */
public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.ViewHolder>{
    private static List<EventHelper> events;
    private Context context;
    private static EventClickListenerInterface mClickListener;

    /**
     * This is a UpcomingEventsAdapter constructor with following parameters.
     * @param events the upcoming events
     * @param clickListener listener
     */
    public UpcomingEventsAdapter(List<EventHelper> events, EventClickListenerInterface clickListener){
        this.events = events;
        this.mClickListener = clickListener;
    }

    /**
     * This method is used to inflate the upcoming events view to update the view.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcomingeventscard, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method is used to bind the updated view with associated data (the events title and date).
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

        holder.itemView.setOnLongClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_box, null);

            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
            dialogBuilder.setView(dialogView);

            TextView deleteButton = dialogView.findViewById(R.id.materialButton2); // Assume your delete button has this ID
            TextView cancelButton = dialogView.findViewById(R.id.materialButton); // Assume your cancel button has this ID

            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            deleteButton.setOnClickListener(view -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String eventId = (String) event.getId();
                if (eventId != null) {
                    db.collection("events").document(eventId).delete().addOnSuccessListener(aVoid -> {
                        events.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, events.size());
                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        // Handle failure
                        dialog.dismiss();
                    });
                }
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());

            return true;
        });
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
     * This view holder child class is used to create the new view holder for the upcoming events.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, date;
        ImageView image;

        /**
         * Defining a click listener for the ViewHolder's View.
         * @param itemView used to initialize the event attributes.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            image = itemView.findViewById(R.id.admin_event_image);
            itemView.setOnClickListener(this);
        }

        /**
         * This method is used to find the upcoming event position in the upcoming events list, that was clicked.
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            Log.d("EventsTodayAdapter", "Item clicked");
            if (mClickListener != null) mClickListener.onEventClick(events.get(getAdapterPosition()));
        }
    }
} // adapter class closing
