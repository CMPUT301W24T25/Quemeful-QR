package com.android.quemeful_qr;

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

/**
 * This is an attendee adapter class used to handle attendee view data.
 */
public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {
    List<Attendee> attendees = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String eventid;

    /**
     * This method is used to fetch all the attendees that are checked in for a specific event and put them in a list
     * @param eventId The checked in event with the specific Id.
     */
    public AttendeeAdapter(String eventId) {
        this.eventid = eventId;
        fetchdata();

    }

    /**
     * This method is used by recyclerview when it creates a new view.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return AttendeeViewHolder
     */
    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_attendee, parent, false);
        return new AttendeeViewHolder(itemView);
    }

    /**
     * This method binds the view with their respective data.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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

    /**
     * This method is used to count the attendees.
     * @return int size (count of attendees)
     */
    @Override
    public int getItemCount() {
        return attendees.size();
    }

    /**
     * AttendeeViewHolder(child) does not have a parameterless constructor,
     * so it needs to call superclass constructors (parent)
     */
    static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView times_checked_in;
        ImageView logo_checked_in;
        AttendeeViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.body_notificationShow);
            times_checked_in = view.findViewById(R.id.times_checked_in);
            logo_checked_in = view.findViewById(R.id.checkin_icon);
        }
    }
// Inside AttendeeAdapter class

    public void refreshData() {

        attendees.clear();
        fetchdata();
    }


    public void fetchdata(){
        DocumentReference eventRef = db.collection("events").document(this.eventid);

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
} // AttendeeAdapter class closing
