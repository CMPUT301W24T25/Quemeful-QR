package com.android.quemeful_qr;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This activity class is used to display the admin view event details.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {
    private TextView textViewEventTitle, textViewEventDate, textViewEventTime, textViewEventLocation, textViewEventDescription;
    private FirebaseFirestore db;
    private ImageView imageViewBackArrow;
    private TextView viewAttendee;

    /**
     * The onCreate() initializes the above declared instances and retrieves the eventID.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
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

    /**
     * This method is used to go to the list of attendees signed up for that event with the eventId and replace the fragment.
     * @param eventId the event with that specific Id.
     */
    private void navigateToListOfAttendees(String eventId) {
        AttendeesList attendeesFragment = new AttendeesList(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, attendeesFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * This method is used to fetch the event details for the event with that specific eventId from the firebase collection.
     * @param eventId the event with that specific Id.
     */
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
} // AdminEventDetailsActivity closing
