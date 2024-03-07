package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Profile extends Fragment {

    private static final String ARG_FIRST_NAME = "firstName";
    private static final String ARG_LAST_NAME = "lastName";

    public Profile
            () {
    }

    public static Profile newInstance(String firstName, String lastName) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_FIRST_NAME, firstName);
        args.putString(ARG_LAST_NAME, lastName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView firstNameTextView = view.findViewById(R.id.firstNameTextView);
        TextView lastNameTextView = view.findViewById(R.id.lastNameTextView);

        if (getArguments() != null) {
            String firstName = getArguments().getString(ARG_FIRST_NAME, "");
            String lastName = getArguments().getString(ARG_LAST_NAME, "");
            firstNameTextView.setText(firstName);
            lastNameTextView.setText(lastName);
        }

        return view;

    }

}
