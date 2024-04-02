package com.android.quemeful_qr;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;


/**
 * This is a fragment class used to handle re-use existing QR code functionality.
 */
public class ReuseQRCodeFragment extends Fragment {
    String eventId;

    /**
     * This is a constructor with eventId as parameter.
     * @param eventId the event with the specific eventId.
     */
    public ReuseQRCodeFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reuse_qrcode, container, false);

        // a close button on the fragment right corner to enable closing the fragment pop up on click
        ImageButton closeButton = view.findViewById(R.id.close_fragment_button);
        closeButton.setOnClickListener(v -> {
            if(getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(ReuseQRCodeFragment.this)
                        .commit();
                // make the frame of the fragment disappear together along with the fragment closing
                FrameLayout fragmentFrame = getActivity().findViewById(R.id.reuse_qr_fragment_container);
                fragmentFrame.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "reuse fragment is not attached to activity create new event");
            }
        });

        // initialize recycler view from xml
        RecyclerView recyclerView = view.findViewById(R.id.reuse_qr_recyclerview);
        // set the list items linearly vertical for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the recycler view adapter and pass the eventId to the adapter
        ReuseQRCodeAdapter adapter = new ReuseQRCodeAdapter(eventId);
        recyclerView.setAdapter(adapter);

        // start reuse Qr code activity to display the selected QR code from list and the associated event title,
        // to enable user to scan it for check-in into this event.
        adapter.setOnUriItemClickListener((view1, position, uri) -> {
            Intent intent = new Intent(getActivity(), ReuseQRCodeActivity.class);
            intent.putExtra("event id", eventId); // pass eventId
            intent.putExtra("Selected QR uri for reuse", uri); // pass the selected QR code uri
            startActivity(intent);
        });

        return view;
    }


} // fragment class closing