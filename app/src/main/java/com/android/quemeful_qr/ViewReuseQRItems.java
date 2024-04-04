package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * This is a (fragment) class used to handle the view for each item,
 * in the list of check-in QR-code uri-strings to the user.
 * (only used for setting up the associated xml layout with this fragment)
 * (no methods implemented within this fragment)
 */
public class ViewReuseQRItems extends Fragment {

    public ViewReuseQRItems() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_reuse_qr_items, container, false);
    }

}