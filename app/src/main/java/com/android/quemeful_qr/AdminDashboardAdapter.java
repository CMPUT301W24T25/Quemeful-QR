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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class AdminDashboardAdapter extends RecyclerView.Adapter<AdminDashboardAdapter.ViewHolder> {
    private List<Map<String, Object>> dataList;
    private Context context;

    public AdminDashboardAdapter(Context context, List<Map<String, Object>> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_dashboard_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> data = dataList.get(position);
        String fullName = data.get("firstName") + " " + data.get("lastName");
        holder.textViewName.setText(fullName);

        String avatarUrl = (String) data.get("avatarUrl");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Log.d("AdminDashboardAdapter", "Successfully deleted event with ID: ");
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.imageViewAvatar);
        }

        holder.itemView.setOnLongClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_box, null);

            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
            dialogBuilder.setView(dialogView);

            TextView deleteButton = dialogView.findViewById(R.id.materialButton2);
            TextView cancelButton = dialogView.findViewById(R.id.materialButton);

            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            deleteButton.setOnClickListener(view -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = (String) data.get("uid");
                if (userId != null) {
                    // Handle events organized by the user
                    List<String> eventsOrganized = (List<String>) data.get("events_organized");
                    if (eventsOrganized != null && !eventsOrganized.isEmpty()) {
                        for (String eventId : eventsOrganized) {
                            db.collection("events").document(eventId).delete().addOnSuccessListener(aVoid -> {
                                Log.d("AdminDashboardAdapter", "Successfully deleted event with ID: " + eventId);
                            }).addOnFailureListener(e -> {
                                Log.e("AdminDashboardAdapter", "Error deleting event with ID: " + eventId, e);
                            });
                        }
                    }

                    // Handle events the user was signed up for
                    List<String> eventsSignedUp = (List<String>) data.get("events");
                    if (eventsSignedUp != null && !eventsSignedUp.isEmpty()) {
                        for (String eventId : eventsSignedUp) {
                            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                                List<Map<String, String>> signedUpList = (List<Map<String, String>>) documentSnapshot.get("signed_up");
                                if (signedUpList != null) {
                                    // Remove the user's uid from the signed_up list
                                    signedUpList.removeIf(signUp -> userId.equals(signUp.get("uid")));
                                    // Update the event document
                                    db.collection("events").document(eventId).update("signed_up", signedUpList)
                                            .addOnSuccessListener(aVoid -> Log.d("AdminDashboardAdapter", "User removed from signed_up list in event: " + eventId))
                                            .addOnFailureListener(e -> Log.e("AdminDashboardAdapter", "Error updating signed_up list in event: " + eventId, e));
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("AdminDashboardAdapter", "Error fetching event with ID: " + eventId, e);
                            });
                        }
                    }
                    // After deleting events, proceed to delete the user
                    db.collection("users").document(userId).delete().addOnSuccessListener(aVoid -> {
                        dataList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, dataList.size());
                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        Log.e("AdminDashboardAdapter", "Error deleting user with ID: " + userId, e);
                        dialog.dismiss();
                    });
                }
            });

            cancelButton.setOnClickListener(view -> dialog.dismiss());
            return true;
        });
        holder.imageViewDelete.setOnClickListener(view -> {
            // URL of the new avatar image
            String newAvatarUrl = "https://firebasestorage.googleapis.com/v0/b/quemeful-qr-8e3a2.appspot.com/o/avatars%2Fe4a01d113899a8fc?alt=media&token=33555359-6857-41a5-9078-eaff94876979";

            // Retrieve the user's document ID. It could be stored as "docId" or "uid" based on your earlier setup.
            String userId = (String) data.get("docId");  // Ensure this key matches what you have in your data map.
            if (userId != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(userId).update("avatarUrl", newAvatarUrl)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("admin_dashboard_adapter", "Avatar URL successfully updated for user: " + userId);
                            // Also update the avatar shown in the adapter.
                            Glide.with(holder.itemView.getContext())
                                    .load(newAvatarUrl)
                                    .into(holder.imageViewAvatar); // Load and set the new image
                        })
                        .addOnFailureListener(e -> Log.e("admin_dashboard_adapter", "Error updating avatar URL", e));
            }
        });
    }



    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateDataList(List<Map<String, Object>> newDataList) {
        this.dataList = newDataList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewAvatar, imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete); // Ensure this matches your layout
        }
    }
}
