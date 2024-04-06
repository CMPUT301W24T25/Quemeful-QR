package com.android.quemeful_qr;


import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has an interface for user/organizer to create new event and enter details for new event
 * implements DatePickerDialog and TimerPickerDialog which are on the interface
 * * References:
 *  URL- <a href="https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/">...</a>
 *  Author- GeeksforGeeks, Published Date- May 17, 2022
 *  URL- <a href="https://www.youtube.com/watch?v=CQ5qcJetYAI">...</a>
 *  Author- Ben O'Brien, Published Date- Apr 20, 2020
 *  URL- <a href="https://www.youtube.com/watch?v=_mo0vPfOaAQ">...</a>
 *  Author- Everyday Programmer, Published Date- Jul 11, 2023
 *  URL- <a href="https://stackoverflow.com/questions/49831751/get-base64-string-from-image-uri">...</a>
 *  Author- Oğuzhan Döngül, License- CC BY-SA 3.0, Published Date- Apr 14, 2018
 *  URL- <a href="https://firebase.google.com/docs/firestore/query-data/queries#java">...</a>
 *  Author- Firebase Documentation, License- CC BY 4.0 and Apache 2.0, Published Date- 2024-03-14 UTC.
 *  URL- <a href="https://stackoverflow.com/questions/41396194/how-to-convert-image-to-string-in-android">...</a>
 *  Author- Dilip Ati, License- CC BY-SA 3.0, Published Date- Nov 28, 2017
 *  URL- <a href="https://www.youtube.com/watch?v=cxEb4IafzZU">...</a>
 *  Author- KD Techs, Published Date- Jun 1, 2022.
 *  URL- <a href="https://developer.android.com/training/data-storage/shared/photopicker">...</a>
 *  Author- Android Developers, License- CC BY 2.5 and Apache 2.0, Published Date- 2024-03-12 UTC.
 */

public class CreateNewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        DatePickerFragment.DatePickerDialogListener,
        TimePickerDialog.OnTimeSetListener,
        TimePickerFragment.TimePickerDialogListener{
    // xml variables
    private TextInputEditText eventTitle, eventDescription;
    private TextView startDate, startTime, endDate, endTime, eventLocation;
    private AppCompatButton generateQRButton, reuseQRButton;
    private ImageButton uploadPoster;

    int LAUNCH_MAP_ACTIVITY = 1;
    // fragment frame
    private FrameLayout reuseFragmentFrame;

    // poster
    private Uri selectedImageUri;

    //firebase
    private FirebaseFirestore db;

    // attributes for event class
    private String eventId, eventName, locationString;
   // private String eventId;
    private boolean startDateTextClicked, endDateTextClicked, startTimeTextClicked, endTimeTextClicked;
    private EventHelper event;
    private Double locationLatitude, locationLongitude;

    /**
     * This method sets the clickable property of the buttons and text boxes for user to enter event details.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // initialize xml variables
        eventTitle = findViewById(R.id.enter_title);
        eventDescription = findViewById(R.id.enter_event_details);
        startDate = findViewById(R.id.enter_startDate);
        startTime = findViewById(R.id.enter_startTime);
        endDate = findViewById(R.id.enter_endDate);
        endTime = findViewById(R.id.enter_endTime);
        generateQRButton = findViewById(R.id.QR_generate_button_for_createEvent);
        reuseQRButton = findViewById(R.id.ReuseQRButton);
        reuseFragmentFrame = findViewById(R.id.reuse_qr_fragment_container);
        reuseFragmentFrame.setVisibility(View.GONE);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button createButton = findViewById(R.id.create_button);
        uploadPoster = findViewById(R.id.add_poster_button);
        eventLocation = findViewById(R.id.enter_location);

        ImageButton limitAttendee = findViewById(R.id.limit_no_of_attendees_buttonIcon);

        //initialize firebase
        db = FirebaseFirestore.getInstance();


        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });
        
        
        // calls the imageChooser() to upload an image/poster for the event,
        // when clicked on the plus icon under 'Add Poster'.
        uploadPoster.setOnClickListener(v -> imageChooser());

        // opens a fragment to pick the starting time of the event.
        startTime.setOnClickListener(v -> {
            startTimeTextClicked = true;
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "start time picker");

        });

        // opens a fragment to pick the ending time of the event.
        endTime.setOnClickListener(v -> {
            endTimeTextClicked = true;
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "end time picker");

        });

        // opens a fragment to pick the starting date of the event.
        startDate.setOnClickListener(v -> {

            startDateTextClicked = true;
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "StartDatePicker");
        });

        // opens a fragment to pick the ending date of the event.
        endDate.setOnClickListener(v -> {
            endDateTextClicked = true;
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "EndDatePicker");
        });

        // after taking user input for a new event, creates the new event using the create button, and
        // reports all those attributes to the event class.
        createButton.setOnClickListener(v -> {
            //generates random id for the event
            eventId = db.collection("events").document().getId();
            eventName = eventTitle.getText().toString();
            String eventTime = startTime.getText().toString();
            String eventDate = startDate.getText().toString();
            String eventDescr = eventDescription.getText().toString();

            try {
                if(selectedImageUri != null) {
                    // converts uri to bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
                    // converts bitmap to base64 string
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // In case you want to compress your image, here it's at 40%
                    bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    // create new event
                     event = new EventHelper(eventId, eventName, locationString, locationLatitude, locationLongitude, eventTime, eventDate, eventDescr, Base64.encodeToString(byteArray, Base64.DEFAULT));
                    //empty poster check
                    Toast message = Toast.makeText(getBaseContext(), "Please add an event poster Image", Toast.LENGTH_LONG);
                    message.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    message.show();
                }
                // create new event
                addNewEvent(event);
                generateQRButton.setVisibility(View.VISIBLE);
                reuseQRButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // on click generates a new QR by starting Generate new QR activity
        generateQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateNewEventActivity.this, GenerateNewQRActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("event name", eventName);
            startActivity(intent);

        });

        // call the navigateToReuseQRFragment method to load the fragment pop up.
        reuseQRButton.setOnClickListener(v -> navigateToReuseQRFragment(eventId));

        // cancels creating new event by closing this activity
        cancelButton.setOnClickListener(v -> {
            Toast.makeText(CreateNewEventActivity.this, "Create New Event Cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });

        // call method to pop up limit attendee dialog fragment
        limitAttendee.setOnClickListener(v -> navigateToLimitAttendeeDialogFragment(eventId));

        // call method to open MapActivity
        eventLocation.setOnClickListener(v -> openMapActivity());

    } // onCreate closing

    /**
     * This method is used to start the MapActivity when map/location button is clicked.
     */
    private void openMapActivity() {
        Intent intent = new Intent(CreateNewEventActivity.this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * This method is used to show the pop up dialog to set limit for attendees.
     */
    private void navigateToLimitAttendeeDialogFragment(String eventId) {
        LimitAttendeeDialogFragment dialogFragment = new LimitAttendeeDialogFragment(eventId);
        dialogFragment.show(getSupportFragmentManager(), "Limit Attendee Dialog");
    }

    /**
     * This method is used to show the fragment pop up with the list of event check in Qr codes,
     * to select one for reuse for the newly created event.
     * It loads the associated reuse fragment.
     * @param eventId the event id (to identify correctly) of the newly created event.
     */
    private void navigateToReuseQRFragment(String eventId) {
        ReuseQRCodeFragment reuseFragment = new ReuseQRCodeFragment(eventId);
        // only before loading the fragment the frame layout as the fragment container should be visible.
        reuseFragmentFrame.setVisibility(View.VISIBLE);
        // Begin a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // replace the fragment container with the fragment data, that appears within the frame.
        transaction.replace(R.id.reuse_qr_fragment_container, reuseFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_MAP_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                locationString = data.getStringExtra("location string");
                locationLatitude = data.getDoubleExtra("location latitude", 0);
                locationLongitude = data.getDoubleExtra("location longitude", 0);

                eventLocation.setText(locationString);
                Toast.makeText(getApplicationContext(), locationLatitude + "," + locationLongitude, Toast.LENGTH_LONG).show();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult

    /**
     * This method is used to tell which time textview (start time or end time) is pressed/clicked on.
     */
    public void setTimeClickFalse(){
        startTimeTextClicked = false;
        endTimeTextClicked = false;
    }

    /**
     * This method is used to tell which date textview (start date or end date) is pressed/clicked on.
     */
    public void setDateClickFalse(){
        startDateTextClicked = false;
        endDateTextClicked = false;
    }

    /**
     * This method is used to add the new event created with all its attributes to the firebase collection db.
     * @param event The new event created that is to be added to the firebase.
     */
    private void addNewEvent(EventHelper event) {

        String eventName = eventTitle.getText().toString();
        String eventLocation = "location";
        String eventTime = startTime.getText().toString();
        String eventDate = startDate.getText().toString();
        String eventDescr = eventDescription.getText().toString();
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if ( eventName.matches("") || eventLocation.matches("")
                || eventTime.matches("") || eventDate.matches("")
                || eventDescr.matches("")){
            //empty string check
            Toast message = Toast.makeText(getBaseContext(), "Please Fill All Text Fields", Toast.LENGTH_LONG);
            message.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            message.show();
        } else {
            HashMap<String, Object> data = new HashMap<>();
            data.put("organizer", currentUserUID);
            data.put("id", event.getId());
            data.put("title", event.getTitle());
            data.put("location", event.getLocation());
            data.put("time", event.getTime());
            data.put("date", event.getDate());
            data.put("description", event.getDescription());

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                            String token = task.getResult().toString();
                            data.put("organizer_token", token);
                        }
                        if (event.getPoster() != null) {
                            data.put("poster", event.getPoster());
                        } else {
                            data.put("poster", ""); // need fix uploading
                        }
                        List<Map<String, Object>> emptySignUpList = new ArrayList<>();
                        data.put("signed_up", emptySignUpList);

                        CollectionReference eventsRef = db.collection("events");
                        eventsRef.document(eventId).set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FireStore", "DocumentSnapshot successfully written!");
                                    Toast.makeText(CreateNewEventActivity.this, "Create New Event Successful", Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }
    

    /**
     * This method sets time in a certain format after user picks a time from the pop out window.
     * @param view the view associated with this listener
     * @param hourOfDay the hour that was set
     * @param minute the minute that was set
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        startTime = findViewById(R.id.enter_startTime);
        if (startTimeTextClicked){
            String startTimeString = String.format("%02d:%02d", hourOfDay, minute);
            startTime.setText(startTimeString);
            startTimeTextClicked = false;
        }else if (endTimeTextClicked){
            String endTimeString = String.format("%02d:%02d", hourOfDay, minute);
            endTime.setText(endTimeString);
            endTimeTextClicked = false;
        }

    }

    /**
     * This method sets date and displays the time selected on the text views.
     * @param view the picker associated with the dialog
     * @param year the selected year
     * @param month the selected month (0-11 for compatibility with {@link Calendar#MONTH})
     * @param dayOfMonth the selected day of the month (1-31, depending on month)
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatDateString = format1.format(calendar.getTime());
        if (startDateTextClicked){
            startDate.setText(formatDateString);
            startDateTextClicked = false;
        }
        else if(endDateTextClicked){
            endDate.setText(formatDateString);
            endDateTextClicked = false;
        }
    }

    /**
     * This method selects an image for the event poster and saves the uri to a variable.
     */
    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
    
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        uploadPoster.setImageURI(selectedImageUri);
                    }
                }

            });

        } // closing CreateNewEventActivity