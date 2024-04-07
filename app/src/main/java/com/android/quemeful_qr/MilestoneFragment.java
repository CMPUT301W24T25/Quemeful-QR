package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.quemeful_qr.R;

public class MilestoneFragment extends Fragment {

    private static final String ARG_MILESTONE = "milestone";
    private static final String ARG_SIGNED_UP = "signed_up";

    private int milestone;
    private int signedUp;

    public MilestoneFragment(int milestone, int signedUp) {
        this.milestone = milestone;
        this.signedUp = signedUp;

    }

    public MilestoneFragment(String eventId) {
    }

    public static MilestoneFragment newInstance(int milestone, int signedUp) {
        MilestoneFragment fragment = new MilestoneFragment(milestone, signedUp);
        Bundle args = new Bundle();
        args.putInt(ARG_MILESTONE, milestone);
        args.putInt(ARG_SIGNED_UP, signedUp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            milestone = getArguments().getInt(ARG_MILESTONE);
            signedUp = getArguments().getInt(ARG_SIGNED_UP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_milstones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView milestoneTextView = view.findViewById(R.id.current_milestone_text);
        if (milestone <= signedUp) {
            milestoneTextView.setText("Milestone reached\uD83C\uDF89 "+ signedUp + "/" + milestone  );
        } else {
            milestoneTextView.setText( "Next Milestone: " + signedUp + "/" + milestone);
        }
    }
}
