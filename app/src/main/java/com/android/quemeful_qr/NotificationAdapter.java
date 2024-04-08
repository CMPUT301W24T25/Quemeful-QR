package com.android.quemeful_qr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.annotation.SuppressLint;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_show_notifications, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView bodyTextView;
        private TextView date_timeTextView;
        private TextView fromTextView;
        private ImageView posterImageView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_notificationShow);
            bodyTextView = itemView.findViewById(R.id.body_notificationShow);
            date_timeTextView = itemView.findViewById(R.id.dateTimeNotification);
            fromTextView = itemView.findViewById(R.id.fromNotification);
            posterImageView = itemView.findViewById(R.id.showNotificationPosterImage);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Notification notification) {
            titleTextView.setText(notification.getTitle());
            bodyTextView.setText(notification.getBody());
            date_timeTextView.setText(notification.getDate_time());
            Log.d(TAG, "bind: " + notification.getDate_time());
            fromTextView.setText(  notification.getFrom() );

//            if (notification.getImage() != null && !notification.getImage().trim().isEmpty()) {
//                byte[] decodedString = Base64.decode(notification.getImage().trim(), Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                posterImageView.setImageBitmap(decodedByte);
//            } else {
//                posterImageView.setImageResource(R.drawable.gradient_background); // Placeholder if no image is present
//            }
            // Use Glide to load the image from the URI
            if (notification.getImage() != null && !notification.getImage().trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(notification.getImage()) // Load the image directly from the URI
                        .placeholder(R.drawable.gradient_background) // Optional: a placeholder until the image loads
                        .error(R.drawable.gradient_background) // Optional: an error image if the load fails
                        .into(posterImageView);
            } else {
                posterImageView.setImageResource(R.drawable.gradient_background); // Placeholder if no image is present
            }



        }
    }
}
