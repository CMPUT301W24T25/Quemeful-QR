package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a class activity that handles the event details view for specific user type.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private TextView textViewEventTitle, textViewEventDate, textViewEventTime, textViewEventLocation, textViewEventDescription, current_milestone_text, congradulatoryText;
    private FirebaseFirestore db;
    private ImageView imageViewBackArrow, imageViewEventImage;
    private TextView viewAttendee, textViewScanQR, textViewSignUp, textViewDeleteEvent; //new
    private Button buttonCheckIn, buttonSignUp, buttonDeleteEvent; //new

    private int[] MILESTONES = {1, 10, 100, 200, 500};

    private CardView milestoneCardView;

    AtomicBoolean isAdminNew = new AtomicBoolean();

    /**
     * This onCreate method is used to set up an interface with all event details.
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
        viewAttendee = findViewById(R.id.viewAttendee);

        milestoneCardView = findViewById(R.id.milestone_cardView);
        current_milestone_text = findViewById(R.id.current_milestone_text);
        congradulatoryText = findViewById(R.id.congratulatory_message);
        imageViewEventImage = findViewById(R.id.imageViewEvent);

        textViewScanQR = findViewById(R.id.scanQRTitle);
        textViewSignUp = findViewById(R.id.signUpTitle);
        textViewDeleteEvent = findViewById(R.id.deleteEventTitle); //new
        buttonCheckIn = findViewById(R.id.scanQRButton);
        buttonSignUp = findViewById(R.id.signUpButton);
        buttonDeleteEvent = findViewById(R.id.deleteEventButton); //new

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //new
        String eventId = getIntent().getStringExtra("event_id");

        //new
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                Boolean isAdminResult = document.getBoolean("Admin");
                if (isAdminResult != null && isAdminResult) {
                    isAdminNew.set(true);
                    Log.d(TAG, "This is an admin: " + deviceId);
                } else {
                    isAdminNew.set(false);
                    Log.d(TAG, "This is not an admin: " + deviceId);
                }
                if (eventId != null) {
                    fetchEventDetails(eventId, isAdminNew);
                    nonAdminFunctionalities(eventId);
                } else {
                    //Handle error
                }

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        //newend
    }

    private void nonAdminFunctionalities(String eventId) {
        setupSignUpButton(eventId);
        setupCheckInButton();

        // on click on viewAttendee it navigates to the list of attendees.
        viewAttendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToListOfAttendees(eventId);
            }
        });

        milestoneCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLMilestone(eventId);
            }
        });

        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) document.get("signed_up");
                    if (signedUpUsers.size() > 0) {

                        congradulatoryText.setVisibility(View.VISIBLE);
                    }

                    int nextMilestone = 0;
                    for (int milestone : MILESTONES) {
                        if (signedUpUsers.size() < milestone) {
                            nextMilestone = milestone;
                            break;
                        }
                    }
                    current_milestone_text.setText( "Next Milestone: " + signedUpUsers.size() + "/" + nextMilestone);
                }}});
    }


    /**
     * This method is used to set the button to sign up for an event.
     * @param eventId the event being signed up for (identified with its specific id).
     */
    private void setupSignUpButton(String eventId) {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpForEvent(eventId);
            }
        });
    }

    /**
     * This method is used to set the button to check-in to an event using the QR code.
     */
    private void setupCheckInButton(){
        buttonCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRCheckActivity();
            }
        });
    }

    /**
     * This method is used to sign up for an event.
     * @param eventId the event being signed up for (identified with its specific id).
     */
    private void signUpForEvent(String eventId) {
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference eventRef = db.collection("events").document(eventId);


        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", currentUserUID);
        userMap.put("checked_in", "0"); // Assuming "0" means not checked-in and "1" means checked-in
        FirebaseMessaging.getInstance().subscribeToTopic(eventId);
        DocumentReference user = db.collection("users").document(currentUserUID);
        user.update("events", FieldValue.arrayUnion(eventId));
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
                    eventRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String title = document.getString("title");
                                List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) document.get("signed_up");
                                String description;
                                for (int milestone : MILESTONES) {
                                    if (document.get("signed_up") != null) {

                                        if (signedUpUsers.size() == milestone) {
                                            if (milestone == 1){
                                                description = "You just got your first attendee!";
                                            } else {
                                                description = "You have reached " + milestone + " attendees!";
                                            }
                                            String token = document.getString("organizer_token");
                                            sendNotif sendNotif = new sendNotif();
                                            sendNotif.sendNotification(title, "You just hit a Milestone!\uD83C\uDF89. " + description, token, "party");
                                        }}}}}});

                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    /**
     * This method is used to navigate to the list of attendees attending a specific event.
     * @param eventId the event being attended (identified with its specific id).
     */
    private void navigateToListOfAttendees(String eventId) {
        AttendeesList attendeesFragment = new AttendeesList(eventId);

        // Begin a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, attendeesFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void navigateToLMilestone(String eventId) {
        milestone milestoneFragment = new milestone(eventId);

        // Begin a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, milestoneFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * This method is used to fetch the event details with its specific id from the firebase,
     * and display them to the user.
     *
     * @param eventId    the event being attended/signed up/checked in (identified with its specific id).
     * @param isAdminNew
     */
    private void fetchEventDetails (String eventId, AtomicBoolean isAdminNew) {

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

                    if (isAdminNew.get()) {
                        updateUIForAdmin();
                    } else {
                        // Update UI for organizer or general user
                        if (currentUserUID.equals(event.getOrganizer())) {
                            updateUIForOrganizer();
                        } else {
                            updateUIForGeneralUser(currentUserUID, event, documentSnapshot);
                        }
                    }

                    // Decode and set the image
                    if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
                        byte[] decodedString = Base64.decode(event.getPoster().trim(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageViewEventImage.setImageBitmap(decodedByte);
                    } else {
                        imageViewEventImage.setImageResource(R.drawable.ic_launcher_background); // Default or placeholder image.
                    }
                }
            } else {}
        }).addOnFailureListener(e -> {
        });
    }

    private void updateUIForAdmin() {
        // Show admin-specific UI elements
        textViewDeleteEvent.setVisibility(View.VISIBLE);
        buttonDeleteEvent.setVisibility(View.VISIBLE);

        textViewScanQR.setVisibility(View.GONE);
        buttonCheckIn.setVisibility(View.GONE);
        textViewSignUp.setVisibility(View.GONE);
        buttonSignUp.setVisibility(View.GONE);
        viewAttendee.setVisibility(View.GONE);
        milestoneCardView.setVisibility(View.GONE);
        current_milestone_text.setVisibility(View.GONE);
        congradulatoryText.setVisibility(View.GONE);
    }

    /**
     * This method is used to change the UI when an attendee signs up for an event.
     */
    private void updateUIForOrganizer() {
        textViewScanQR.setVisibility(View.GONE);
        buttonCheckIn.setVisibility(View.GONE);
        textViewSignUp.setVisibility(View.GONE);
        buttonSignUp.setVisibility(View.GONE);
        textViewDeleteEvent.setVisibility(View.GONE);
        buttonDeleteEvent.setVisibility(View.GONE);
        viewAttendee.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used to update the UI for a non-attendee.
     * @param currentUserUID the id to identify the current user (attendee/organiser/admin).
     * @param event the event in concern.
     * @param documentSnapshot the list that records events,
     *                        used to fetch status of whether the user is signed up to the event or not.
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
     * This method is used to change the visibility when a user is signed up.
     * @param isUserSignedUp checks the status whether the user is signed-up or not.
     * @param isUserCheckedIn checks the status whether the user is checked-in or not.
     */
    private void updateUIBasedOnUserStatus(boolean isUserSignedUp, boolean isUserCheckedIn) {
        TextView textViewScanQR = findViewById(R.id.scanQRTitle);
        Button buttonCheckIn = findViewById(R.id.scanQRButton);
        TextView textViewSignUp = findViewById(R.id.signUpTitle);
        Button buttonSignUp = findViewById(R.id.signUpButton);
        CardView milestone = findViewById(R.id.milestone_cardView);

        textViewDeleteEvent.setVisibility(View.GONE);
        buttonDeleteEvent.setVisibility(View.GONE);

        if (isUserSignedUp) {
            textViewSignUp.setVisibility(View.GONE);
            buttonSignUp.setVisibility(View.GONE);
            viewAttendee.setVisibility(View.GONE);
            milestone.setVisibility(View.GONE);

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
            milestone.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used when the user clicks on the check-in button to scan the QR code.
     * It works by starting another activity that handles QR code scanning.
     */
    protected void openQRCheckActivity(){
        Intent intent = new Intent(EventDetailsActivity.this, ScanQRActivity.class);
        startActivity(intent);

    }

    /**
     * This method is used to handle permission from user on receiving notifications about the specific event.
     * @param requestCode The request code passed.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
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
} // class closing