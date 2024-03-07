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

    public Events
            () {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        // Inflate the layout for this fragment
        createEventButton = view.findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // allow user to select image from gallery
                openCreateNewEventActivity();
            }
        });

        return view;

    }
    protected void openCreateNewEventActivity(){
        Intent intent = new Intent(Events.this.getActivity(), CreateNewEventActivity.class);
        startActivity(intent);
    }


}
