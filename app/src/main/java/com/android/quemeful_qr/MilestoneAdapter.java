package com.android.quemeful_qr;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.ViewHolder> {

    private int[] MILESTONES = {1, 10, 100, 200, 500};
    private String eventId;
    private  int signedUp = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int[] BACKGROUND_IMAGES = {R.drawable.milestone_1,
            R.drawable.milestone_10,
            R.drawable.milestone_100,
            R.drawable.milestone_200,
            R.drawable.milestone_500};

    private static final int[] BACKGROUND_IMAGES_GRAYSCALE = {R.drawable.milestone_1_grayscale,
            R.drawable.milestone_10_grayscale,
            R.drawable.milestone_100_grayscale,
            R.drawable.milestone_200_grayscale,
            R.drawable.milestone_500_grayscale};

    public MilestoneAdapter(String eventId) {
        this.eventId = eventId;
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) document.get("signed_up");
                        signedUp = signedUpUsers.size();
                       notifyDataSetChanged();
                    }

            }
        });

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_milstones, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int milestone = MILESTONES[position];
        holder.milestoneTextView.setText(signedUp + "/" + milestone + " Signed-up Attendees");
        if (signedUp >= milestone) {
            holder.milestoneImageView.setImageResource(BACKGROUND_IMAGES[position]);
        } else {
            holder.milestoneImageView.setImageResource(BACKGROUND_IMAGES_GRAYSCALE[position]);
            holder.milestoneTextView.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {

        return MILESTONES.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView milestoneTextView;
        public ImageView milestoneImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            milestoneTextView = itemView.findViewById(R.id.current_milestone_text);
            milestoneImageView = itemView.findViewById(R.id.background_image_milstone);
        }
    }
}
