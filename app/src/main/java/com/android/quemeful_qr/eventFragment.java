package com.android.quemeful_qr;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class eventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private SharedViewModel sharedViewModel;
    private String deviceUserId;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar selectedDate = Calendar.getInstance();
    private int selectedTabPosition = 0;

    public eventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            startActivity(intent);
        });
        eventsRecyclerView.setAdapter(eventAdapter);

        db = FirebaseFirestore.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        deviceUserId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        sharedViewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            fetchEventsForSelectedTab(selectedTabPosition);
        });

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

        return view;
    }

    private void fetchEventsForSelectedTab(int tabIndex) {
        String formattedDate = sdf.format(selectedDate.getTime());

        String eventsKey = (tabIndex == 0) ? "events_organized" : "events";
        db.collection("users").document(deviceUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                List<String> eventIds = (List<String>) task.getResult().get(eventsKey);
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
                if (task.isSuccessful() && task.getResult().exists()) {
                    EventHelper event = task.getResult().toObject(EventHelper.class);
                    if (event != null && formattedDate.equals(event.getDate())) {
                        eventsData.add(event);
                    }
                } else {
                    showToast("Error getting event details");
                }

                // This check ensures that the adapter is updated after processing each event
                if (eventsData.size() == eventIds.size() || eventsData.isEmpty()) {
                    eventAdapter.setEvents(eventsData);
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
