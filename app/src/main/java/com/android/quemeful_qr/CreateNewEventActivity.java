//https://stackoverflow.com/a/10407371
package com.android.quemeful_qr;

/**
 * References:
 *  URL- https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
 *  Author- GeeksforGeeks, Published Date- May 17, 2022
 *  URL- https://www.youtube.com/watch?v=CQ5qcJetYAI
 *  Author- Ben O'Brien, Published Date- Apr 20, 2020
 *  URL- https://www.youtube.com/watch?v=_mo0vPfOaAQ
 *  Author- Everyday Programmer, Published Date- Jul 11, 2023
 *  URL- https://stackoverflow.com/questions/49831751/get-base64-string-from-image-uri
 *  Author- Oğuzhan Döngül, License- CC BY-SA 3.0, Published Date- Apr 14, 2018
 *  URL- https://firebase.google.com/docs/firestore/query-data/queries#java
 *  Author- Firebase Documentation, License- CC BY 4.0 and Apache 2.0, Published Date- 2024-03-14 UTC.
 *  URL- https://stackoverflow.com/questions/41396194/how-to-convert-image-to-string-in-android
 *  Author- Dilip Ati, License- CC BY-SA 3.0, Published Date- Nov 28, 2017
 *  URL- https://www.youtube.com/watch?v=cxEb4IafzZU
 *  Author- KD Techs, Published Date- Jun 1, 2022.
 *  URL- https://developer.android.com/training/data-storage/shared/photopicker
 *  Author- Android Developers, License- CC BY 2.5 and Apache 2.0, Published Date- 2024-03-12 UTC.
 */

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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This class has an interface for user/organizer to create new event and enter details for new event
 * implements DatePickerDialog and TimerPickerDialog which are on the interface
 */

public class CreateNewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        DatePickerFragment.DatePickerDialogListener,
        TimePickerDialog.OnTimeSetListener,
        TimePickerFragment.TimePickerDialogListener{
    // xml variables
    private EditText eventTitle;
    private EditText eventDescription;
    private TextView eventLocation;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private Button generateQRButton;
    private Button cancelButton;
    private Button createButton;
    private ImageButton uploadPoster;

    int LAUNCH_MAP_ACTIVITY = 1;

    // poster
    private Uri selectedImageUri;

    //firebase
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    // attributes for event class
    private String eventUUID;
    private boolean startDateTextClicked;
    private boolean endDateTextClicked;
    private boolean startTimeTextClicked;
    private boolean endTimeTextClicked;
    private EventHelper event;

    private Location location;
    private String locationString;
    private Double locationLatitude;
    private Double locationLongitude;

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
     * This method sets the clickable property of the buttons and text boxes for user to enter event details.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // from xml
        eventTitle = findViewById(R.id.enter_title);
        eventDescription = findViewById(R.id.enter_event_details);
        startDate = findViewById(R.id.enter_startDate);
        startTime = findViewById(R.id.enter_startTime);
        endDate = findViewById(R.id.enter_endDate);
        endTime = findViewById(R.id.enter_endTime);
        generateQRButton = findViewById(R.id.QR_generate_button_for_createEvent);

        cancelButton = findViewById(R.id.cancel_button);
        createButton = findViewById(R.id.create_button);
        uploadPoster = findViewById(R.id.add_poster_button);
        eventLocation = findViewById(R.id.enter_location);


        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        /**
         * This method calls the imageChooser() to upload an image/poster for the event, when clicked on the plus icon under 'Add Poster'.
         */
        uploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });
        /**
         * This method to set the start time opens a fragment to pick the starting time of the event.
         */
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeTextClicked = true;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "start time picker");

            }
        });


        /**
         * This method to set the end time opens a fragment to pick the ending time of the event.
         */
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimeTextClicked = true;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "end time picker");

            }
        });

        /**
         * This method to set the start date opens a fragment to pick the starting date of the event.
         */
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDateTextClicked = true;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "StartDatePicker");
            }
        });

        /**
         * This method to set the end date opens a fragment to pick the ending date of the event.
         */
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDateTextClicked = true;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "EndDatePicker");
            }
        });

        /**
         * This method after taking user input for a new event, creates the new event using the create button, and
         * reports all those attributes to the event class.
         */
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generates random id which is the QR code
                eventUUID = UUID.randomUUID().toString();

                String eventName = eventTitle.getText().toString();
                String eventTime = startTime.getText().toString();
                String eventDate = startDate.getText().toString();

                String eventDescr = eventDescription.getText().toString();
                try {
                    // converts uri to bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
                    // converts bitmap to base64 string
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // In case you want to compress your image, here it's at 40%
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    //create new event
                    event = new EventHelper(eventUUID, eventName, "location", eventTime, eventDate, eventDescr, Base64.encodeToString(byteArray, Base64.DEFAULT));
                    addNewEvent(event);
                    generateQRButton.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

              }
        });

        /**
         * This method helps to use the generate button which on click generates a new QR code when required for the new event created.
         * Since, for generating a QR code there exists a separate activity,
         * on clicking on this generate button it starts the GenerateNewQRActivity.
         */
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewEventActivity.this, GenerateNewQRActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);

            }
        });

        /**
         * This method is used when the user decides not to create a new event and wants to cancel, and go back to the previous page.
         * On clicking the cancel button, it finishes this activity.
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateNewEventActivity.this, "Create New Event Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_MAP_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                locationString = data.getStringExtra("location string");
                locationLatitude = data.getDoubleExtra("location latitude", 0);
                locationLongitude = data.getDoubleExtra("location longitude", 0);
                location = new Location();
                location.setName(locationString);
                location.setLatitude(locationLatitude);
                location.setLongitude(locationLongitude);

                eventLocation.setText(location.getName());
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
     * This method is used to start the MapActivity when map/location button is clicked.
     */
    protected void openMapActivity(){
        Intent intent = new Intent(CreateNewEventActivity.this, MapActivity.class);
        startActivityForResult(intent, LAUNCH_MAP_ACTIVITY);

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
        String eventLocation = location.getName();
        String eventTime = startTime.getText().toString();
        String eventDate = startDate.getText().toString();
        String eventDescr = eventDescription.getText().toString();
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (eventUUID.matches("") || eventLocation.matches("") || eventTime.matches("") || eventDate.matches("") || eventDescr.matches("")){
            //empty string check
            Toast myToast = Toast.makeText(CreateNewEventActivity.this, "please enter all fields", Toast.LENGTH_SHORT);
            myToast.show();
        }
        else {
            HashMap<String, Object> data = new HashMap<>();
            data.put("organizer",currentUserUID);
            data.put("id", event.getId());
            data.put("title", event.getTitle());
            data.put("location", event.getLocation());
            data.put("time", event.getTime());
            data.put("date", event.getDate());
            data.put("description", event.getDescription());
            if (event.getPoster() != null) {
                data.put("poster", event.getPoster());
            }
            else {
                data.put("poster", "");
            }
            List<Map<String, Object>> emptySignUpList = new ArrayList<>();
            data.put("signed_up", emptySignUpList);

            eventsRef
                    .document(db.collection("events").document().getId())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "DocumentSnapshot successfully written!");
                            Toast.makeText(CreateNewEventActivity.this, "Create New Event Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
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
        String selectedDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()); //shows Thursday, March 28, 2024
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