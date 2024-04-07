package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is an adapter class used to put the events list organized that day, and
 * all the upcoming events in recyclerview.
 */
public class EventsTodayAdapter extends RecyclerView.Adapter<EventsTodayAdapter.EventViewHolder>{
    private static List<EventHelper> events;
    private Context context;
    private static EventClickListenerInterface mClickListener;

    private boolean isAdmin;

    /**
     * EventsTodayAdapter constructor with parameters.
     * @param context Context
     * @param events All events on that day or upcoming.
     */
    public EventsTodayAdapter(Context context, List<EventHelper> events, EventClickListenerInterface clickListener, boolean isAdmin){
        this.context = context;
        this.events = events;
        this.mClickListener = clickListener;
        this.isAdmin = isAdmin;
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
        return new EventViewHolder(view, isAdmin);
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

//        if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
//            byte[] decodedString = Base64.decode(event.getPoster().trim(), Base64.DEFAULT);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            holder.eventImage.setImageBitmap(decodedByte);
//        } else {
//            holder.eventImage.setImageResource(R.drawable.gradient_background); // Placeholder if no image is present
//        }
        // Use Glide to load the image from the URI
        if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
            Glide.with(context)
                    .load(event.getPoster()) // Load the image directly from the URI
                    .placeholder(R.drawable.gradient_background) // Optional: a placeholder until the image loads
                    .error(R.drawable.gradient_background) // Optional: an error image if the load fails
                    .into(holder.eventImage);
        } else {
            // Use a default image if no poster URI is present
            holder.eventImage.setImageResource(R.drawable.gradient_background);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        holder.itemView.setOnLongClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_box, null);

            if (isAdmin) {
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
                dialogBuilder.setView(dialogView);

                TextView deleteButton = dialogView.findViewById(R.id.materialButton2); // Assume your delete button has this ID
                TextView cancelButton = dialogView.findViewById(R.id.materialButton); // Assume your cancel button has this ID

                final android.app.AlertDialog dialog = dialogBuilder.create();
                dialog.show();

                deleteButton.setOnClickListener(view -> {
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
            }
            return true;
        });

        holder.posterDelete.setOnClickListener(view -> {
            // URL of the new poster
            String newPoster = "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";
            holder.eventImage.setImageResource(R.drawable.empty_poster);

            String eventId = (String) event.getId();
            if (eventId != null) {
                db.collection("events").document(eventId).update("poster", newPoster)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("EventsTodayAdapter for admin", "Poster successfully deleted for event: " + event);
                            // Update poster shown in adapter
                            Glide.with(holder.itemView.getContext())
                                    .load(R.drawable.empty_poster)
                                    .into(holder.eventImage);
                        })
                        .addOnFailureListener(e -> Log.e("EventsTodayAdapter for admin", "Error deleting poster", e));
            }
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
     * This event view holder child class is used to create the new view holder for events with its details.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView eventImage, posterDelete;
        TextView eventTitle, eventLocation, eventTime;

        /**
         * Defining a click listener for the ViewHolder's View.
         * @param itemView used to initialize the event attributes.
         */
        public EventViewHolder(View itemView, boolean isAdmin) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventTime = itemView.findViewById(R.id.eventTime);
            itemView.setOnClickListener(this);
            posterDelete = itemView.findViewById(R.id.posterDelete);
            if (!isAdmin) {
                posterDelete.setVisibility(View.GONE);
            } else {
                posterDelete.setVisibility(View.VISIBLE);
            }
//            itemView.setOnClickListener(this);
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
