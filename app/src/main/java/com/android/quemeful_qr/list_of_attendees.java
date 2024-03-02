package com.android.quemeful_qr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link list_of_attendees#newInstance} factory method to
 * create an instance of this fragment.
 */
public class list_of_attendees extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    private String Eventid;

    public list_of_attendees(String eventid) {
        this.Eventid = Eventid;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment list_of_attendees.
     */
    // TODO: Rename and change types and number of parameters
    public list_of_attendees newInstance(String eventId) {
        list_of_attendees fragment = new list_of_attendees(this.Eventid);

        return fragment;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_attendees, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.list_of_attendees_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // replace "eventId" with the actual event id
        RecyclerView.Adapter adapter = new attendeeAdapter(Eventid);
        recyclerView.setAdapter(adapter);
        TextView totalAttendeeTextView = view.findViewById(R.id.total_attendee);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(Eventid);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> users = (List<String>) document.get("users");
                    if (users != null) {
                        totalAttendeeTextView.setText(String.valueOf(users.size()));
                    }
                }
            }
        });
        ImageButton back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // implement back button functionality
                    }
        });
        ImageButton announcement_button = view.findViewById(R.id.megaphone_button);
        announcement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment announcementFragment = new announcement(Eventid);

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
}