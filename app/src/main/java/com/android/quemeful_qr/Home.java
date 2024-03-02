package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Date;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment implements EventClickListenerInterface{

    public Home() {
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView eventsRecyclerView;
    private RecyclerView upcomingEventsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        upcomingEventsRecyclerView = view.findViewById(R.id.upcoming_events_recycler_view);
        upcomingEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<EventHelper> todayEvents = new ArrayList<>();
                List<EventHelper> upcomingEvents = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {

                    EventHelper event = document.toObject(EventHelper.class);

                    event.setId(document.getId()); // Set the document ID as the event ID
                    try {
                        Date eventDate = DateUtils.parseDate(event.getDate());
                    }
                    catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing event date", e);

                    }
                     Date eventDate = DateUtils.parseDate("01/01/2022");

                    if (eventDate != null) {
                        if (DateUtils.isToday(eventDate)) {
                            todayEvents.add(event);
                        } else if (eventDate.after(new Date())) {
                            upcomingEvents.add(event);
                        }
                    }
                }
                updateUI(todayEvents, upcomingEvents);
            } else {
                // Handle errors
            }
        });
    }

    private void updateUI(List<EventHelper> todayEvents, List<EventHelper> upcomingEvents) {
        if (!todayEvents.isEmpty()) {
            EventsTodayAdapter todayEventAdapter = new EventsTodayAdapter(getActivity(), todayEvents, this);
            eventsRecyclerView.setAdapter(todayEventAdapter);
        } else {

        }

        if (!upcomingEvents.isEmpty()) {
            UpcomingEventsAdapter upcomingEventAdapter = new UpcomingEventsAdapter(upcomingEvents, this);
            upcomingEventsRecyclerView.setAdapter(upcomingEventAdapter);
        } else {

        }
    }

    @Override
    public void onEventClick(EventHelper event) {
        Log.d("HomeFragment", "Event clicked: " + event.getTitle());
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }

}
