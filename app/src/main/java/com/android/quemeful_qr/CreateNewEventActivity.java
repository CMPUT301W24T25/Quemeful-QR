//https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
//https://www.youtube.com/watch?v=CQ5qcJetYAI
//https://www.youtube.com/watch?v=_mo0vPfOaAQ
//https://stackoverflow.com/questions/49831751/get-base64-string-from-image-uri
//https://firebase.google.com/docs/firestore/query-data/queries#java
//https://stackoverflow.com/q/41396194
package com.android.quemeful_qr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Draft copy, code to be reviewed again.
 * This activity creates and new event.
 * Yet to do: 1. add firebase to store the new event.
 *            2. add the "Use Existing QR Code" part (see mockup create event page).
 *            3. write test cases
 *           (4) needs to handle empty field cases by displaying a toast "please fill all fields" before creating event.
 * Reference URLs (yet to specify author, license,... will add them later. This is just a record.)
 * https://www.youtube.com/watch?v=cxEb4IafzZU
 * https://developer.android.com/training/data-storage/shared/photopicker
 */
public class CreateNewEventActivity extends AppCompatActivity {
    // declare variables
//    private TextInputEditText eventTitle;
//    private TextInputEditText eventDescription;
//    private TextInputEditText startDate;
//    private TextInputEditText startTime;
//    private TextInputEditText endDate;
//    private TextInputEditText endTime;
    private EditText eventTitle;
    private EditText eventDescription;
    private EditText startDate;
    private EditText startTime;
    private EditText endDate;
    private EditText endTime;
    private Button generateQRButton;
    private Button cancelButton;
    private Button createButton;
    private ImageButton uploadPoster;
    //for date and time picker
//    Calendar calendar = Calendar.getInstance();
//    Calendar time = Calendar.getInstance();
    private Uri selectedImageUri;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String eventUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // initialize variables
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
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        // for adding poster by user added READ_MEDIA_IMAGES permission in AndroidManifest.xml
        // start image selecting activity and get its result using ActivityResultLauncher
//        ActivityResultLauncher<PickVisualMediaRequest> pickImage = registerForActivityResult(
//                new ActivityResultContracts.PickVisualMedia(),
//                uri -> {
//                    if (uri != null) {
//                        // display the selected image
//                        uploadPoster.setImageURI(uri);
//                    }
//                }
//        );

        // initiate selection of image when the add button is clicked using Intent
        // add poster
//        uploadPoster.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // allow user to select image from gallery
//                pickImage.launch(new PickVisualMediaRequest.Builder()
//                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                        .build());
//            }
//        });
        uploadPoster.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventUUID = UUID.randomUUID().toString();
                String eventName = eventTitle.getText().toString();
                String eventLocation = "location";


                String eventTime = startTime.getText().toString();
                String eventDate = startDate.getText().toString();
                String eventDescr = eventDescription.getText().toString();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // In case you want to compress your image, here it's at 40%
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    EventHelper event = new EventHelper(eventUUID, eventName, eventLocation, eventTime, eventDate, eventDescr, Base64.encodeToString(byteArray, Base64.DEFAULT));
                    addNewEvent(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

              }
        });

        // set listener on create button
//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // retrieve user input for event title and details
//                String title = eventTitle.getText().toString();
//                String description = eventDescription.getText().toString();
//
//
//
//                // set the retrieved details in their respective text boxes
//                eventTitle.setText(title);
//                eventDescription.setText(description);
//                // call selectDate and selectTime
//                selectDate();
//                selectTime();
//                Toast.makeText(CreateNewEventActivity.this, "New Event successfully created", Toast.LENGTH_SHORT).show();
//            }
//        });

        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewEventActivity.this, GenerateNewQRActivity.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateNewEventActivity.this, "Create New Event Cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateNewEventActivity.this, Home.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This method is to get the start and the end date from user.
     * selectDate() allows user to pick date.
     * setDateFormat() is a method used to set the format for displaying the date in dd/MM/yyyy.
     */
//    private void selectDate(){
//        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                calendar.set(Calendar.YEAR, year);
//                calendar.set(Calendar.MONTH, month);
//                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                // set the date in its fields
//                startDate.setText(setDateFormat());
//                endDate.setText(setDateFormat());
//            }
//        };

        // start date picker and end date picker works the same way
//        startDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(CreateNewEventActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
//
//        endDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(CreateNewEventActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
//    }
    private void addNewEvent(EventHelper event) {

        String eventName = eventTitle.getText().toString();
        String eventLocation = "location";
        String eventTime = startTime.getText().toString();
        String eventDate = startDate.getText().toString();
        String eventDescr = eventDescription.getText().toString();

        if (eventUUID.matches("") || eventName.matches("") || eventLocation.matches("")
        || eventTime.matches("") || eventDate.matches("")|| eventDescr.matches("")){ //empty string
            Toast myToast = Toast.makeText(CreateNewEventActivity.this, "please enter all fields", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
            HashMap<String, Object> data = new HashMap<>();
            String parsedTime = DateUtils.formatTime(event.getTime());

            String parsedDate = DateUtils.formatDate(event.getDate());
            data.put("Event ID", event.getId());
            data.put("Event Title", event.getTitle());
            data.put("Event Location", event.getLocation());
            data.put("Event Time", parsedTime);
            data.put("Event Date", parsedDate);
            data.put("Event Description", event.getDescription());
            data.put("Event Poster", event.getPoster());
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


//    private String setDateFormat(){
//        String format = "dd/MM/yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CANADA);
//        return simpleDateFormat.format(calendar.getTime());
//    }

    /**
     * This method is to get the start and the end time from user.
     * selectTime() allows user to pick time.
     * setTimeFormat() is a method used to set the format for displaying the time in HH:mm.
     */
//    private void selectTime(){
//        int hours = time.get(Calendar.HOUR_OF_DAY);
//        int minutes = time.get(Calendar.MINUTE);
//        TimePickerDialog.OnTimeSetListener timePickerDialog = new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                time.set(Calendar.HOUR_OF_DAY, hours);
//                time.set(Calendar.MINUTE, minutes);
//
//                // set the times in their fields
//                startTime.setText(setTimeFormat());
//                endTime.setText(setTimeFormat());
//            }
//        };

//        startTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new TimePickerDialog(CreateNewEventActivity.this, (TimePickerDialog.OnTimeSetListener) time, hours, minutes, true).show();
//            }
//        });
//        endTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new TimePickerDialog(CreateNewEventActivity.this, (TimePickerDialog.OnTimeSetListener) time, hours, minutes, true).show();
//            }
//        });
//    }
//    private String setTimeFormat(){
//        String format = "HH:mm";
//        SimpleDateFormat timeFormat = new SimpleDateFormat(format, Locale.CANADA);
//        return timeFormat.format(time.getTime());
//    }
}