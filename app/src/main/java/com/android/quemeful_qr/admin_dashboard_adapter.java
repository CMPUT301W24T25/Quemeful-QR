package com.android.quemeful_qr;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class admin_dashboard_adapter extends RecyclerView.Adapter<admin_dashboard_adapter.ViewHolder> {
    private List<Map<String, Object>> dataList;
    private Context context;

    public admin_dashboard_adapter(Context context, List<Map<String, Object>> dataList) {
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
            if (avatarUrl.endsWith(".svg") || avatarUrl.contains("avataaars.io")) {
                loadSvgFromUrl(holder.imageViewAvatar, avatarUrl);
            } else {
                Glide.with(holder.itemView.getContext()).load(avatarUrl).into(holder.imageViewAvatar);
            }
        } else {
            holder.imageViewAvatar.setImageResource(R.drawable.scan_logo);
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
                String userId = (String) data.get("uid");
                if (userId != null) {
                    db.collection("users").document(userId).delete().addOnSuccessListener(aVoid -> {
                        dataList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, dataList.size());
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
        ImageView imageViewAvatar;
        ImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);  // Ensure this ID matches your XML
        }
    }

    private void loadSvgFromUrl(final ImageView imageView, final String urlString) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                InputStream inputStream = connection.getInputStream();
                SVG svg = SVG.getFromInputStream(inputStream);
                final PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                imageView.post(() -> imageView.setImageDrawable(drawable));
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
