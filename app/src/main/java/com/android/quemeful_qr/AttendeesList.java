package com.android.quemeful_qr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * This is a fragment subclass.
 * Use the {@link AttendeesList#newInstance} factory method to create an instance of this fragment.
 */
public class AttendeesList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    private String Eventid;
    private String EventName;

    /**
     * This is a constructor with eventid as parameter.
     * @param eventid The checked in event with the specific Id.
     */
    public AttendeesList(String eventid) {
        this.Eventid = eventid;
        // Required empty public constructor
    }

    /**
     * This method is used to create a new instance of the AttendeesList class.
     * @return A new instance of fragment list_of_attendees.
     */
    // TODO: Rename and change types and number of parameters
    public AttendeesList newInstance(String eventId) {
        AttendeesList fragment = new AttendeesList(this.Eventid);

        return fragment;
    }

    /**
     * This method is used to make a recyclerview with list of attendees.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return View
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_attendees, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.Show_Notification_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // replace "eventId" with the actual event id
        RecyclerView.Adapter adapter = new AttendeeAdapter(Eventid);
        recyclerView.setAdapter(adapter);
        TextView totalAttendeeTextView = view.findViewById(R.id.total_attendee);
        TextView check_inTextView = view.findViewById(R.id.total_checked_in);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(Eventid);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, String>> users = (List<Map<String, String>>) document.get("signed_up");
                    EventName = (String) document.get("title");
                    if (users != null) {
                        totalAttendeeTextView.setText("Total Attendee: " + users.size());
                    }
                    Integer checkedIn = 0;
                    for (Map<String, String> user : users) {
                       checkedIn += Integer.parseInt( user.get("checked_in"));
                        }

                    check_inTextView.setText("Check-in: " + checkedIn);
                    }
            }

        });


        ImageButton back_button = view.findViewById(R.id.back_button);

        /**
         * The back button on click navigates user back to the previous page.
         */
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // implement back button functionality
                // Check if the FragmentManager has any entries in the back stack
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                    // Pop the current fragment from the back stack
                    getFragmentManager().popBackStack();
                } else {
                    // Optionally, if there is no entry in the back stack, you might want to handle it differently.
                    // For example, you could exit the activity if this is the last fragment in the stack.
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        ImageButton announcement_button = view.findViewById(R.id.megaphone_button);

        /**
         * The announcement/megaphone button on click opens up a new announcement fragment.
         */
        announcement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment announcementFragment = new Announcement(Eventid, EventName);

                // Use the FragmentManager to start a FragmentTransaction
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace the current fragment with the new announcement fragment
                transaction.replace(R.id.fragment_container, announcementFragment);

                // Add the transaction to the back stack so the user can navigate back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });
        return view;
    }

} // AttendeeList fragment closing