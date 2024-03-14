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
import android.provider.Settings;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        String eventLocation = "location"; // This should be dynamic or user-defined in the actual implementation.
        String eventTime = startTime.getText().toString();
        String eventDate = startDate.getText().toString();
        String eventDescr = eventDescription.getText().toString();
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (eventUUID.isEmpty() || eventName.isEmpty() || eventLocation.isEmpty()
                || eventTime.isEmpty() || eventDate.isEmpty() || eventDescr.isEmpty()) {
            Toast.makeText(CreateNewEventActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        } else {
            final String eventId = db.collection("events").document().getId();
            HashMap<String, Object> data = new HashMap<>();
            data.put("organizer", currentUserUID);
            data.put("id", eventId);
            data.put("title", eventName);
            data.put("location", eventLocation);
            data.put("time", eventTime);
            data.put("date", eventDate);
            data.put("description", eventDescr);
            data.put("poster", event.getPoster());

            eventsRef.document(eventId).set(data).addOnSuccessListener(aVoid -> {
                Log.d("Firestore", "DocumentSnapshot successfully written!");
                updateUserEvents(currentUserUID, eventId);
            }).addOnFailureListener(e -> {
                Log.e("Firestore", "Error writing document", e);
                Toast.makeText(CreateNewEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateUserEvents(String userId, String eventId) {
        CollectionReference usersRef = db.collection("users");
        usersRef.document(userId)
                .update("events_organized", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateNewEventActivity.this, "Event created and user updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error updating user", e);
                    Toast.makeText(CreateNewEventActivity.this, "Failed to update user events", Toast.LENGTH_SHORT).show();
                });
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