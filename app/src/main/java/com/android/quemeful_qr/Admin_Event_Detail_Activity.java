package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.firestore.FirebaseFirestore;

public class Admin_Event_Detail_Activity extends AppCompatActivity {

    private TextView textViewEventTitle, textViewEventDate, textViewEventTime, textViewEventLocation, textViewEventDescription;
    private FirebaseFirestore db;
    private ImageView imageViewBackArrow;
    private TextView viewAttendee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetailsactivity);

        db = FirebaseFirestore.getInstance();

        imageViewBackArrow = findViewById(R.id.backArrow);
        imageViewBackArrow.setOnClickListener(v -> finish());

        textViewEventTitle = findViewById(R.id.textViewEventTitle);
        textViewEventDate = findViewById(R.id.textViewEventDate);
        textViewEventTime = findViewById(R.id.textViewEventTime);
        textViewEventLocation = findViewById(R.id.textViewEventLocation);
        textViewEventDescription = findViewById(R.id.textViewEventDescription);
        viewAttendee = findViewById(R.id.viewAttendee);

        // Ensure the key here matches what's used in the adapter when putting the extra
        String eventId = getIntent().getStringExtra("eventId");  // Changed "event_id" to "eventId"
        if (eventId != null) {
            fetchEventDetails(eventId);
            viewAttendee.setOnClickListener(v -> navigateToListOfAttendees(eventId));
        } else {
            // Handle the case where eventId is null or missing
            finish(); // Close the activity or inform the user of the error
        }
    }

    private void navigateToListOfAttendees(String eventId) {
        list_of_attendees attendeesFragment = new list_of_attendees(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, attendeesFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchEventDetails(String eventId) {
        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                EventHelper event = documentSnapshot.toObject(EventHelper.class);
                if (event != null) {
                    textViewEventTitle.setText(event.getTitle());
                    textViewEventDate.setText(event.getDate());
                    textViewEventTime.setText(event.getTime());
                    textViewEventLocation.setText(event.getLocation());
                    textViewEventDescription.setText(event.getDescription());
                }
            } else {
                // Handle the case where the event doesn't exist in the database
            }
        }).addOnFailureListener(e -> {
            // Log the error or inform the user
        });
    }
}
