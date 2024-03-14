package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Events extends Fragment {

    Button createEventButton;

    public Events() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events, container, false);

        // Inflate the layout for this fragment
        createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // allow user to select image from gallery
                openCreateNewEventActivity();
            }
        });

        addCalendarFragment();
        addEventFragment();

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
