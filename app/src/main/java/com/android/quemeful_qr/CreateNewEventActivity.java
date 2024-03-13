//https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
//https://www.youtube.com/watch?v=CQ5qcJetYAI
//https://www.youtube.com/watch?v=_mo0vPfOaAQ
//https://stackoverflow.com/questions/49831751/get-base64-string-from-image-uri
//https://firebase.google.com/docs/firestore/query-data/queries#java
//https://stackoverflow.com/q/41396194
//https://www.youtube.com/watch?v=33BFCdL0Di0
//https://www.youtube.com/watch?v=cxEb4IafzZU
// https://developer.android.com/training/data-storage/shared/photopicker
package com.android.quemeful_qr;

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
import java.util.UUID;

/**
 * This class has an interface for user/organizer to create new event and enter details for new event
 * implements DatePickerDialog and TimerPickerDialog which are on the interface
 */

public class CreateNewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        DatePickerFragment.DatePickerDialogListener, TimePickerDialog.OnTimeSetListener, TimePickerFragment.TimePickerDialogListener{
    // xml variables
    private EditText eventTitle;
    private EditText eventDescription;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private Button generateQRButton;
    private Button cancelButton;
    private Button createButton;
    private ImageButton uploadPoster;

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

    /**
     * sets time in a certain format after user picks a time from the pop out window
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
     * Sets up the clickable buttons and textboxes so user can enter event details
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
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

        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        //displays selected pic on app
        uploadPoster.setOnClickListener(new View.OnClickListener() {
            /**
             * picks an image to display onto the screen
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            /**
             * opens fragment for timepicker
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                startTimeTextClicked = true;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "start time picker");

            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            /**
             * opens fragment for datepicker
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                endTimeTextClicked = true;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "end time picker");

            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            /**
             * opens fragment for datepicker
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                startDateTextClicked = true;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "StartDatePicker");
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            /**
             * opens fragment for datepicker
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                endDateTextClicked = true;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "EndDatePicker");
            }
        });

        //creates event
        createButton.setOnClickListener(new View.OnClickListener() {
            /**
             * takes all the information user/organizer entered and puts all
             * attributes to event class
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                //generates random id which is the QR code
                eventUUID = UUID.randomUUID().toString();

                String eventName = eventTitle.getText().toString();
                String eventLocation = "location";
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
                    event = new EventHelper(eventUUID, eventName, eventLocation, eventTime, eventDate, eventDescr, Base64.encodeToString(byteArray, Base64.DEFAULT));
                    addNewEvent(event);
                    generateQRButton.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

              }
        });


        generateQRButton.setOnClickListener(new View.OnClickListener() {
            /**
             * goes to next page that shows the QR code
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewEventActivity.this, GenerateNewQRActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);

            }
        });
        /**
         * closes the window and goes back to previous page
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateNewEventActivity.this, "Create New Event Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * this is to tell which textview (end time or start time) is pressed
     */
    public void setTimeClickFalse(){
        endTimeTextClicked = false;
        startTimeTextClicked = false;

    }

    /**
     * this is to tell which textview (end time or start time) is pressed
     */
    public void setDateClickFalse(){
        endDateTextClicked = false;
        startDateTextClicked = false;
    }

    /**
     * adds new event with the attributes to the firebase
     * @param event
     */
    private void addNewEvent(EventHelper event) {

        String eventName = eventTitle.getText().toString();
        String eventLocation = "location";
        String eventTime = startTime.getText().toString();
        String eventDate = startDate.getText().toString();
        String eventDescr = eventDescription.getText().toString();
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (eventUUID.matches("") || eventLocation.matches("") || eventTime.matches("") || eventDate.matches("") || eventDescr.matches("")){ //empty string
            Toast myToast = Toast.makeText(CreateNewEventActivity.this, "please enter all fields", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
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
     * sets date and displays the time selected on the textviews
     * @param view the picker associated with the dialog
     * @param year the selected year
     * @param month the selected month (0-11 for compatibility with
     *              {@link Calendar#MONTH})
     * @param dayOfMonth the selected day of the month (1-31, depending on
     *                   month)
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
        }else if(endDateTextClicked){
            endDate.setText(formatDateString);
            endDateTextClicked = false;
        }



    }

    /**
     * pick a pic to display on app and save the Uri in a variable
     */
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);

    }
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null
                            && data.getData() != null) {
                        selectedImageUri = data.getData();
                        uploadPoster.setImageURI(selectedImageUri);

                    }

                }
            });



}