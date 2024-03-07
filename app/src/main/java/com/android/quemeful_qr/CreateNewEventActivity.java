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
    // xml variables
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

    // poster
    private Uri selectedImageUri;

    //firebase
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    // attributes for event class
    private String eventUUID;
    private String parsedTime;
    private String parsedDate;

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
//        generateQRButton.setVisibility();
        cancelButton = findViewById(R.id.cancel_button);
        createButton = findViewById(R.id.create_button);
        uploadPoster = findViewById(R.id.add_poster_button);

        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        //displays selected pic on app
        uploadPoster.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
        //creates event
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generates random id which is the QR code
                eventUUID = UUID.randomUUID().toString();

                String eventName = eventTitle.getText().toString();
                String eventLocation = "location";
                String eventTime = startTime.getText().toString();
                String eventDate = startDate.getText().toString();

                //formats time for display
                parsedTime = DateUtils.formatTime(eventTime);
                Log.d("check time", parsedTime);

                //formats date for display
                parsedDate = DateUtils.formatDate(eventDate);

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
                    EventHelper event = new EventHelper(eventUUID, eventName, eventLocation, parsedTime, parsedDate, eventDescr, Base64.encodeToString(byteArray, Base64.DEFAULT));
                    addNewEvent(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

              }
        });


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
                finish();
            }
        });
    }

    /**
     * This method is to get the start and the end date from user.
     * selectDate() allows user to pick date.
     * setDateFormat() is a method used to set the format for displaying the date in dd/MM/yyyy.
     */

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
            data.put("id", event.getId());
            data.put("title", event.getTitle());
            data.put("location", event.getLocation());
            data.put("time", parsedTime);
            data.put("date", parsedDate);
            data.put("description", event.getDescription());
            data.put("poster", event.getPoster());
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



}