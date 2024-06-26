package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Date;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle view shown on clicking on Home button in the app,
 * that displays events occurring on that day and other upcoming events in any.
 */
public class Home extends Fragment implements EventClickListenerInterface{

    public String deviceId;
    public boolean isAdmin;
    /**
     * This is a default Home constructor (no parameters).
     */
    public Home(String deviceId, boolean isAdmin) {
        this.deviceId = deviceId;
        this.isAdmin = isAdmin;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView eventsRecyclerView;
    TextView noUpcomingEventsTextView;
    TextView noTodayEventsTextView;
    private RecyclerView upcomingEventsRecyclerView;
    Date eventDate;

    /**
     * This method is used to initialize the declared instances and call the fetchEvents() to fetch events from firebase.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView textViewDashboard = view.findViewById(R.id.textViewDashboard);

        if (!isAdmin) {
            textViewDashboard.setText("Dashboard");
        } else {
            textViewDashboard.setText("Events");
        }

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        upcomingEventsRecyclerView = view.findViewById(R.id.upcoming_events_recycler_view);
        upcomingEventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        noTodayEventsTextView = view.findViewById(R.id.noTodayEventsTextView);
        noUpcomingEventsTextView = view.findViewById(R.id.noUpcomingEventsTextView);



        fetchEvents();

        return view;
    }

    /**
     * This method is used to fetch events from the firebase collection and sort them by their dates.
     */
    private void fetchEvents() {
        System.out.println(db.collection("events").toString());
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<EventHelper> todayEvents = new ArrayList<>();
                List<EventHelper> upcomingEvents = new ArrayList<>();
                
                for (QueryDocumentSnapshot document : task.getResult()) {

//                    EventHelper event = document.toObject(EventHelper.class);


//
                    String eventId = document.getString("id");
                    String eventName = document.getString("title");
                    String eventLocation = document.getString("location");
                    Double eventLatitude = document.getDouble("latitude");
                    Double eventLongitude = document.getDouble("longitude");
                    String eventTime = document.getString("time");
                    String eventDateString = document.getString("date");

                    String eventDescr = document.getString("description");
                    String eventPoster = document.getString("poster");
                    String currentUserUID = document.getString("organizer");
                    EventHelper event = new EventHelper(eventId, eventName, eventLocation,eventLatitude,
                            eventLongitude,eventTime,eventDateString,eventDescr, eventPoster);


                    event.setId(document.getId()); // Set the document ID as the event ID
                    try {
                        Log.d("Date", event.getDate());
                        Date eventDate = DateUtils.parseDate(event.getDate());

                        if (eventDate != null) {

                            if (DateUtils.isToday(eventDate)) {
                                todayEvents.add(event);
                            } else if (eventDate.after(new Date())) {
                                upcomingEvents.add(event);
                            }
                        }
                    }
                    catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing event date", e);

                    }
                     //Date eventDate = DateUtils.parseDate(event.getDate());


                }
                Log.d("today", todayEvents.toString());
                Log.d("upcoming", upcomingEvents.toString());
                updateUI(todayEvents, upcomingEvents);
            } else {
                // Handle errors
            }
        });
    }

    /**
     * This method is used to update the interface with events on that day and the upcoming events if any.
     * @param todayEvents the events on that day.
     * @param upcomingEvents the upcoming events.
     */
    private void updateUI(List<EventHelper> todayEvents, List<EventHelper> upcomingEvents) {

        if (!todayEvents.isEmpty()) {
            noTodayEventsTextView.setVisibility(View.GONE);
            EventsTodayAdapter todayEventAdapter = new EventsTodayAdapter(getActivity(), todayEvents, this, isAdmin);
            eventsRecyclerView.setAdapter(todayEventAdapter);
        }
        else {
            noTodayEventsTextView.setVisibility(View.VISIBLE);
        }

        if (!upcomingEvents.isEmpty()) {
            noUpcomingEventsTextView.setVisibility(View.GONE);
            UpcomingEventsAdapter upcomingEventAdapter = new UpcomingEventsAdapter(upcomingEvents, this, isAdmin);
            upcomingEventsRecyclerView.setAdapter(upcomingEventAdapter);
        }
        else {
            noUpcomingEventsTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method is used to navigate to the event details when an event is clicked in the list of events.
     * @param event the event clicked.
     */
    @Override
    public void onEventClick(EventHelper event) {
        if (!isAdmin) {
            Log.d("HomeFragment", "Event clicked: " + event.getTitle());
            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
            intent.putExtra("event_id", event.getId());
            startActivity(intent);
        } else {
            Log.d("HomeFragment", "Admin Event clicked: " + event.getTitle());
            Intent intent = new Intent(getActivity(), AdminEventDetailsActivity.class);
            intent.putExtra("event_id", event.getId());
            startActivity(intent);
        }
    }

} // class closing
