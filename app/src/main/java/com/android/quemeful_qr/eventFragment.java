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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class eventFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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

        // Using device ID as the user ID
        String deviceUserId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        fetchUserOrganizedEvents(deviceUserId);

        return view;
    }

    private void fetchUserOrganizedEvents(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> eventIds = (List<String>) documentSnapshot.get("events_organized");
            if (eventIds != null && !eventIds.isEmpty()) {
                fetchEventsDetails(eventIds);
            } else {
                Toast.makeText(getContext(), "No events organized by user", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> showToast("Error getting user events: " + e.getMessage()));
    }

    private void fetchEventsDetails(List<String> eventIds) {
        List<Map<String, Object>> eventsData = new ArrayList<>();

        for (String eventId : eventIds) {
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> event = documentSnapshot.getData();
                if (event != null) {
                    eventsData.add(event);
                }
                if (eventsData.size() == eventIds.size()) {
                    eventAdapter.setEvents(eventsData); // Update adapter data
                }
            }).addOnFailureListener(e -> showToast("Error getting event details: " + e.getMessage()));
        }
    }

    private void showToast(String message) {
        if (isAdded()) { // Check if the fragment is currently added to its activity
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
