package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * make new fragment that contains admin view's event list
 */
public class admin_event_list_fragment extends Fragment {
    private RecyclerView recyclerView;
    private admin_event_adapter adapter;
    private List<Map<String, Object>> events;

    /**
     * takes events and puts them in recycler view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_events, container, false);

        recyclerView = view.findViewById(R.id.admin_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize events list
        events = new ArrayList<>();

        // Fetch events from Firebase
        fetchEvents();

        adapter = new admin_event_adapter(getContext(), events);
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * gets events from firebase
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert document data to Map and add to list
                            Map<String, Object> eventData = document.getData();
                            events.add(eventData);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle error
                    }
                });
    }
}
