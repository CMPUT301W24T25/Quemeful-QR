package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import org.osmdroid.views.MapView;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * This class is used to set up create a new event button and handle the location map.
 */
public class Events extends Fragment {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    Button createEventButton;


    /**
     * Events default constructor (no parameters)
     */
    public Events() {}

    /**
     * This onCreate method is used to set up a create event button,
     * to navigate to the CreateNewEventActivity to create a new event.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events, container, false);

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


    private void openCreateNewEventActivity() {
        Intent intent = new Intent(getActivity(), CreateNewEventActivity.class);
        startActivity(intent);
    }

    private void addCalendarFragment() {
        calender_fragment calendarFragment = new calender_fragment();

        FragmentManager fragmentManager = getChildFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.calendar_container, calendarFragment);

        fragmentTransaction.commit();
    }

    private void addEventFragment() {
        // Here you can add your event fragment using FragmentTransaction
        // For demonstration purposes, let's assume you have an eventFragment already implemented
        eventFragment eventFragment = new eventFragment();

        FragmentManager fragmentManager = getChildFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.event_container, eventFragment);

        fragmentTransaction.commit();
    }

}


