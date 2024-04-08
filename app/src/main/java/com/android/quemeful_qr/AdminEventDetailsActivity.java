package com.android.quemeful_qr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * This is a class activity that handles the event details view for specific user type.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {

    private TextView textViewEventTitle, textViewEventDate, textViewEventTime, textViewEventLocation, textViewEventDescription;
    private FirebaseFirestore db;
    private ImageView imageViewBackArrow, imageViewEventImage;

    /**
     * This onCreate method is used to set up an interface with all event details.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_details);

        db = FirebaseFirestore.getInstance();

        imageViewBackArrow = findViewById(R.id.backArrow);
        textViewEventLocation = findViewById(R.id.textViewEventLocation);

        // navigate back to previous page on clicking the back arrow.
        imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textViewEventTitle = findViewById(R.id.textViewEventTitle);
        textViewEventDate = findViewById(R.id.textViewEventDate);
        textViewEventTime = findViewById(R.id.textViewEventTime);
        textViewEventLocation = findViewById(R.id.textViewEventLocation);
        textViewEventDescription = findViewById(R.id.textViewEventDescription);
        imageViewEventImage = findViewById(R.id.imageViewEvent);


        String eventId = getIntent().getStringExtra("event_id");
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //new

        if (eventId != null) {
            fetchEventDetails(eventId);
        } else {
            // Handle the error
        }
    }


    /**
     * This method is used to fetch the event details with its specific id from the firebase,
     * and display them to the user.
     *
     * @param eventId the event being attended/signed up/checked in (identified with its specific id).
     */
    private void fetchEventDetails(String eventId) {

        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                EventHelper event = documentSnapshot.toObject(EventHelper.class);
                if (event != null) {
                    textViewEventTitle.setText(event.getTitle());
                    textViewEventDate.setText(event.getFormattedDate());
                    textViewEventTime.setText(event.getTime());
                    textViewEventLocation.setText(event.getLocation());
                    textViewEventDescription.setText(event.getDescription());

                    // Use Glide to load the image from a URL
                    if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
                        Glide.with(AdminEventDetailsActivity.this)
                                .load(event.getPoster())
                                .into(imageViewEventImage);
                    } else {
                        imageViewEventImage.setImageResource(R.drawable.ic_launcher_background); // Default or placeholder image.
                    }
                }
            } else {
            }
        }).addOnFailureListener(e -> {
        });
    }
}