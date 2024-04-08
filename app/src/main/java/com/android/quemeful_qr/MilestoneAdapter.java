package com.android.quemeful_qr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class MilestoneAdapter extends FragmentPagerAdapter {

    private int[] MILESTONES = {1, 10, 100, 200, 500};

    private int signedUp;

    public MilestoneAdapter(@NonNull FragmentManager fm, int signedUp) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.signedUp = signedUp;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Create a new instance of MilestoneFragment and pass milestone number and signed up count
        return MilestoneFragment.newInstance(MILESTONES[position], signedUp);
    }

    @Override
    public int getCount() {
        return MILESTONES.length;
    }
}
