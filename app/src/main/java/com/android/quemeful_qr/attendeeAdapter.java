package com.android.quemeful_qr;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class attendeeAdapter extends RecyclerView.Adapter<attendeeAdapter.AttendeeViewHolder> {
    private List<Attendee> attendees = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public attendeeAdapter(String eventId) {
        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) document.get("signed_up");
                    for (Map<String, Object> userMap : signedUpUsers) {
                        String attendeeId = (String) userMap.get("uid");
                        Integer checkedIn =  Integer.parseInt( userMap.get("checked_in").toString());
                        DocumentReference userRef = db.collection("users").document(attendeeId);
                        userRef.get().addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                DocumentSnapshot userDocument = userTask.getResult();
                                if (userDocument.exists()) {
                                    String firstName = userDocument.getString("firstName");
                                    String lastName = userDocument.getString("lastName");
                                    attendees.add(new Attendee(attendeeId, firstName, lastName, checkedIn));
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_attendee, parent, false);
        return new AttendeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        if (!attendees.isEmpty()) {
            Attendee attendee = attendees.get(position);
            if (holder.nameTextView != null) {
                holder.nameTextView.setText(String.format("%s %s", attendee.getFirstName(), attendee.getLastName()));
            }
            if (holder.times_checked_in != null) {
                if (attendee.getCheckedIn() == 0) {
                    holder.logo_checked_in.setImageResource(R.drawable.check_mark_empty);
                } else {
                    holder.times_checked_in.setText(String.format("Check-in\n%d times", attendee.getCheckedIn()));
                    holder.logo_checked_in.setImageResource(R.drawable.check_mark_filled);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return attendees.size();
    }

    static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView times_checked_in;
        ImageView logo_checked_in;

        AttendeeViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.name_of_attendee);
            times_checked_in = view.findViewById(R.id.times_checked_in);
            logo_checked_in = view.findViewById(R.id.checkin_icon);
        }
    }
}
