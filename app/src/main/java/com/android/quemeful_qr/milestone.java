package com.android.quemeful_qr;



import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class milestone extends Fragment {

    private String eventId;

    private RecyclerView recyclerView;
    private milstoneAdapter adapter;

    public milestone(String eventId) {
        this.eventId = eventId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_milstones_list, container, false);

        adapter = new milstoneAdapter(eventId);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);



        ImageView imageView = view.findViewById(R.id.backArrow);
        imageView.setOnClickListener(v -> {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {

                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}
