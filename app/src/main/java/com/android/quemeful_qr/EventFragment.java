package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventFragment extends Fragment implements EventClickListenerInterface{

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private SharedViewModel sharedViewModel;
    private String deviceUserId;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar selectedDate = Calendar.getInstance();
    private int selectedTabPosition = 0;

    private String currentFetchDate = "";

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        initializeRecyclerView(view);
        setupFirebase();
        setupSharedViewModel();
        setupTabLayout(view);

        return view;
    }

    private void initializeRecyclerView(View view) {
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(getActivity(),new ArrayList<>(), event -> {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            intent.putExtra("event_id", event.getId());
            startActivity(intent);
        });
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        deviceUserId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void setupSharedViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            fetchEventsForSelectedTab(selectedTabPosition);
        });
    }

    private void setupTabLayout(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Events Organized"));
        tabLayout.addTab(tabLayout.newTab().setText("Checked in"));
        tabLayout.getTabAt(selectedTabPosition).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                fetchEventsForSelectedTab(selectedTabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                fetchEventsForSelectedTab(tab.getPosition());
            }
        });
    }

    private void fetchEventsForSelectedTab(int tabIndex) {
        String formattedDate = sdf.format(selectedDate.getTime());
        currentFetchDate = formattedDate;
        String eventsKey = (tabIndex == 0) ? "events_organized" : "events";

        db.collection("users").document(deviceUserId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                showToast("Error listening for event updates: " + e.getMessage());
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                List<String> eventIds = (List<String>) documentSnapshot.get(eventsKey);
                if (eventIds != null && !eventIds.isEmpty()) {
                    fetchEventsDetails(eventIds, formattedDate);
                } else {
                    showToast("No events available for selected date");
                    eventAdapter.setEvents(new ArrayList<>());
                }
            } else {
                showToast("Error getting user events");
            }
        });
    }

    private void fetchEventsDetails(List<String> eventIds, String formattedDate) {
        List<EventHelper> eventsData = new ArrayList<>();

        for (String eventId : eventIds) {
            db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
                if (!formattedDate.equals(currentFetchDate)) {
                    // If the fetch date does not match the current fetch date, ignore this result
                    return;
                }
                if (task.isSuccessful() && task.getResult().exists()) {
                    EventHelper event = task.getResult().toObject(EventHelper.class);
                    if (event != null && formattedDate.equals(event.getDate())) {
                        eventsData.add(event);
                    }
                } else {
                    showToast("Error getting event details");
                }

                // Since tasks are asynchronous, check again before updating the UI
                if (formattedDate.equals(currentFetchDate)) {
                    if (eventsData.size() == eventIds.size() || eventsData.isEmpty()) {
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

    @Override
    public void onEventClick(EventHelper event) {
        Log.d("Event Fragment", "Event clicked: " + event.getTitle());
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }
}
