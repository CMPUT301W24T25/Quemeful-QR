package com.android.quemeful_qr;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class eventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private SharedViewModel sharedViewModel;
    private String deviceUserId;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar selectedDate = Calendar.getInstance(); // Default to today, can be updated via SharedViewModel.

    public eventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(getContext(), new ArrayList<>());
        eventsRecyclerView.setAdapter(eventAdapter);

        db = FirebaseFirestore.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        deviceUserId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Observe selected date changes.
        sharedViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date; // Update the current selected date.
            fetchEventsForSelectedDate(); // Refetch events whenever the selected date changes.
        });

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Events Organized"));
        tabLayout.addTab(tabLayout.newTab().setText("Checked in"));

        fetchEventsForSelectedDate(); // Initial fetch for events.

        return view;
    }

    private void fetchEventsForSelectedDate() {
        String formattedDate = sdf.format(selectedDate.getTime());

        db.collection("users").document(deviceUserId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                showToast("Error getting user events: " + e.getMessage());
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<String> eventIds = (List<String>) documentSnapshot.get("events_organized");
                if (eventIds != null && !eventIds.isEmpty()) {
                    fetchEventsDetails(eventIds, formattedDate);
                } else {
                    showToast("No events organized by user");
                    eventAdapter.setEvents(new ArrayList<>()); // Clear events as there are none.
                }
            }
        });
    }

    private void fetchEventsDetails(List<String> eventIds, String formattedDate) {
        List<Map<String, Object>> eventsData = new ArrayList<>();

        for (String eventId : eventIds) {
            db.collection("events").document(eventId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                showToast("Error getting event details: " + e.getMessage());
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                Map<String, Object> event = documentSnapshot.getData();
                                if (event != null && event.containsKey("date") && formattedDate.equals(event.get("date"))) {
                                    eventsData.add(event);
                                } else {
                                    // If the event doesn't match the date or any other condition, handle accordingly.
                                    // This else block could be empty if you only want to add matching events.
                                }
                                // This ensures the UI is updated after each document is evaluated, which may not be efficient.
                                // Consider aggregating changes and updating the UI less frequently.
                                eventAdapter.setEvents(eventsData);
                            }
                        }
                    });
        }
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
