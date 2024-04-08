package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is a new fragment that contains admin view's event list.
 */
public class AdminEventFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminEventAdapter adapter;
    List<Map<String, Object>> events;

    /**
     * This method retrieves the events from the firebase collection by calling fetchEvents() and,
     * sets them to the recyclerview.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
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

        adapter = new AdminEventAdapter(getContext(), events);
        recyclerView.setAdapter(adapter);
        return view;
    }

    /**
     * This method is used to fetch events from the firebase collection.
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

} // class fragment closing
