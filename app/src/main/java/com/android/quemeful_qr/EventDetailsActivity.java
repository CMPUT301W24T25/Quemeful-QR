package com.android.quemeful_qr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {


    private TextView textViewEventTitle, textViewEventDate, textViewEventTime, textViewEventLocation, textViewEventDescription;
    private FirebaseFirestore db;
    private ImageView imageViewBackArrow, imageViewEventImage;
    private TextView viewAttendee, textViewScanQR, textViewSignUp;
    private Button buttonCheckIn, buttonSignUp;;


    /**
     * Sets up interface with event details
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetailsactivity);

        db = FirebaseFirestore.getInstance();

        imageViewBackArrow = findViewById(R.id.backArrow);
        textViewEventLocation = findViewById(R.id.textViewEventLocation);
        imageViewBackArrow.setOnClickListener(new View.OnClickListener() {
            /**
             * back arrow closes the page and goes to previous page
             * @param v The view that was clicked.
             */
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
        viewAttendee = findViewById(R.id.viewAttendee);
        imageViewEventImage = findViewById(R.id.imageViewEvent);

        textViewScanQR = findViewById(R.id.scanQRTitle);
        textViewSignUp = findViewById(R.id.signUpTitle);
        buttonCheckIn = findViewById(R.id.scanQRButton);
        buttonSignUp = findViewById(R.id.signUpButton);



        String eventId = getIntent().getStringExtra("event_id");

        if (eventId != null) {
            fetchEventDetails(eventId);
            setupSignUpButton(eventId);
            setupCheckInButton();
            /**
             *
             */
            viewAttendee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToListOfAttendees(eventId);
                }
            });
        } else {
            // Handle the error
        }
    }

    private void setupSignUpButton(String eventId) {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpForEvent(eventId);
            }
        });
    }
    private void setupCheckInButton(){
        buttonCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRCheckActivity();
            }
        });
    }

    private void signUpForEvent(String eventId) {
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference eventRef = db.collection("events").document(eventId);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", currentUserUID);
        userMap.put("checked_in", "0"); // Assuming "0" means not checked-in and "1" means checked-in

        eventRef.update("signed_up", FieldValue.arrayUnion(userMap))
                .addOnSuccessListener(aVoid -> {
                    // Update UI to reflect that the user has signed up
                    Toast.makeText(EventDetailsActivity.this, "Signed up for event successfully!", Toast.LENGTH_SHORT).show();
                    updateUIBasedOnUserStatus(true, false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    /**
     * replace an existing fragment in a container with an instance of a new
     * fragment class
     * @param eventId
     */
    private void navigateToListOfAttendees(String eventId) {
        list_of_attendees attendeesFragment = new list_of_attendees(eventId);

        // Begin a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, attendeesFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * gets event details from the firebase and shows them on this page
     * @param eventId
     */
    private void fetchEventDetails (String eventId) {

        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                EventHelper event = documentSnapshot.toObject(EventHelper.class);
                if (event != null) {
                    textViewEventTitle.setText(event.getTitle());
                    textViewEventDate.setText(event.getDate());
                    textViewEventTime.setText(event.getTime());
                    textViewEventLocation.setText(event.getLocation());
                    textViewEventDescription.setText(event.getDescription());


                    // Update UI for organizer or general user
                    if (currentUserUID.equals(event.getOrganizer())) {
                        updateUIForOrganizer();
                    } else {
                        updateUIForGeneralUser(currentUserUID, event, documentSnapshot);
                    }

                    // Decode and set the image
                    if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
                        byte[] decodedString = Base64.decode(event.getPoster().trim(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageViewEventImage.setImageBitmap(decodedByte);
                    } else {
                        imageViewEventImage.setImageResource(R.drawable.ic_launcher_background); // Default or placeholder image.
                    }

                    // Now, check if the user is signed up and/or checked in
//                    List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) documentSnapshot.get("signed_up");
//                    boolean isUserSignedUp = false;
//                    boolean isUserCheckedIn = false;
//
//                    if (signedUpUsers != null) {
//                        for (Map<String, Object> userMap : signedUpUsers) {
//                            String uid = (String) userMap.get("uid");
//                            String checkedIn = (String) userMap.get("checked_in");
//                            if (currentUserUID.equals(uid)) {
//                                isUserSignedUp = true;
//                                isUserCheckedIn = "1".equals(checkedIn);
//                                break;
//                            }
//                        }
//                    }

                    // Check if user is signed up or not and display UI accordingly
//                    updateUIBasedOnUserStatus(isUserSignedUp, isUserCheckedIn);

                }
            } else {

            }
        }).addOnFailureListener(e -> {
        });
    }

    /**
     * changes the UI when attendee signs up
     */
    private void updateUIForOrganizer() {
        textViewScanQR.setVisibility(View.GONE);
        buttonCheckIn.setVisibility(View.GONE);
        textViewSignUp.setVisibility(View.GONE);
        buttonSignUp.setVisibility(View.GONE);
        viewAttendee.setVisibility(View.VISIBLE);
    }

    /**
     * changes the UI when the attendee signs up
     * @param currentUserUID
     * @param event
     * @param documentSnapshot
     */
    private void updateUIForGeneralUser(String currentUserUID, EventHelper event, DocumentSnapshot documentSnapshot) {
        // Your existing logic to check if the user is signed up or checked in...
        List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) documentSnapshot.get("signed_up");
        boolean isUserSignedUp = false;
        boolean isUserCheckedIn = false;

        if (signedUpUsers != null) {
            for (Map<String, Object> userMap : signedUpUsers) {
                String uid = (String) userMap.get("uid");
                String checkedIn = (String) userMap.get("checked_in");
                if (currentUserUID.equals(uid)) {
                    isUserSignedUp = true;
                    isUserCheckedIn = "1".equals(checkedIn);
                    break;
                }
            }
        }

        updateUIBasedOnUserStatus(isUserSignedUp, isUserCheckedIn);
    }

    /**
     * if user signed up, changes the interface element visibility
     * @param isUserSignedUp
     * @param isUserCheckedIn
     */
    private void updateUIBasedOnUserStatus(boolean isUserSignedUp, boolean isUserCheckedIn) {
        TextView textViewScanQR = findViewById(R.id.scanQRTitle);
        Button buttonCheckIn = findViewById(R.id.scanQRButton);
        TextView textViewSignUp = findViewById(R.id.signUpTitle);
        Button buttonSignUp = findViewById(R.id.signUpButton);

        if (isUserSignedUp) {
            textViewSignUp.setVisibility(View.GONE);
            buttonSignUp.setVisibility(View.GONE);
            viewAttendee.setVisibility(View.GONE);

            if (isUserCheckedIn) {
                textViewScanQR.setVisibility(View.GONE);
                buttonCheckIn.setVisibility(View.GONE);
                // Possibly show a message saying "You are checked in"
            } else {
                textViewScanQR.setVisibility(View.VISIBLE);
                buttonCheckIn.setVisibility(View.VISIBLE);
            }
        } else {
            textViewScanQR.setVisibility(View.GONE);
            buttonCheckIn.setVisibility(View.GONE);
            viewAttendee.setVisibility(View.GONE);
            textViewSignUp.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.VISIBLE);
        }
    }

    /**
     * switches from eventdetailsactivity to QRcheckactivity when button is pressed
     */
    protected void openQRCheckActivity(){
        Intent intent = new Intent(EventDetailsActivity.this, QRCheckActivity.class);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications not allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}