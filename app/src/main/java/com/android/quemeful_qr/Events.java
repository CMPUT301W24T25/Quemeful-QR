package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public class Events extends Fragment {

    Button createEventButton;

    /**
     * constructor
     */
    public Events
            () {
    }

    /**
     * sets up create event button to open a new activity
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        // Inflate the layout for this fragment
        createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCreateNewEventActivity();
            }
        });

        return view;

    }

    /**
     * opens createneweventactivity when button is pressed
     */
    protected void openCreateNewEventActivity(){
        Intent intent = new Intent(Events.this.getActivity(), CreateNewEventActivity.class);
        startActivity(intent);
    }


}
