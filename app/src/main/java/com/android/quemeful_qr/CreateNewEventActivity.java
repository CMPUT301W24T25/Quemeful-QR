package com.android.quemeful_qr;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private EditText inputLimit;
    private TextView startDate, startTime, endDate, endTime, eventLocation;
    private AppCompatButton generateQRButton, reuseQRButton;
    private ImageButton uploadPoster, limitAttendeeButton;

    int LAUNCH_MAP_ACTIVITY = 1;
    // fragment frame
    private FrameLayout reuseFragmentFrame;

    // poster
    private Uri selectedImageUri;

    //firebase
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    // attributes for event class
    private String eventId, eventName, eventTime, eventDate, eventDescr, eventPost, locationString;
    private boolean startDateTextClicked;
    private boolean endDateTextClicked;
    private boolean startTimeTextClicked;
    private boolean endTimeTextClicked;
    private EventHelper event;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
   // private String eventId;
    private Double locationLatitude, locationLongitude;
    private Boolean updateLimitMethodCalled = false;

    private ActivityResultLauncher<String> mGetContent;

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

        limitAttendeeButton = findViewById(R.id.limit_no_of_attendees_buttonIcon);

        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        //generates firebase id for the event
        eventId = db.collection("events").document().getId();

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });

        // calls the imageChooser() to upload an image/poster for the event,
        // when clicked on the plus icon under 'Add Poster'.
//        uploadPoster.setOnClickListener(v -> imageChooser());

        // Initialize the ActivityResultLauncher
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                uploadPoster.setImageURI(uri); // Show the selected image on the ImageButton or ImageView
//                uploadImageToFirebaseStorage(uri); // Proceed to upload after image is selected
            }
        });

        uploadPoster.setOnClickListener(v -> mGetContent.launch("image/*")); // Open image chooser when button is clicked




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
//            try {
//                if(selectedImageUri != null) {
//                    // converts uri to bitmap
//                    uploadImageToFirebaseStorage(selectedImageUri);
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
//                    // converts bitmap to base64 string
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    // In case you want to compress your image, here it's at 40%
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 40, byteArrayOutputStream);
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                    eventPost = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                    generateQRButton.setVisibility(View.VISIBLE);
//                } else {
//                    //empty poster check
//                    Toast message = Toast.makeText(getBaseContext(), "Please add an event poster Image", Toast.LENGTH_LONG);
//                    message.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                    message.show();
//                }
//                // create new event
//                addNewEvent();
//                generateQRButton.setVisibility(View.VISIBLE);
//                reuseQRButton.setVisibility(View.VISIBLE);
//                limitAttendeeButton.setVisibility(View.VISIBLE);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            // create new event
////            addNewEvent();
////            generateQRButton.setVisibility(View.VISIBLE);
////            reuseQRButton.setVisibility(View.VISIBLE);
////            limitAttendeeButton.setVisibility(View.VISIBLE);

            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(selectedImageUri);
                generateQRButton.setVisibility(View.VISIBLE);
                reuseQRButton.setVisibility(View.VISIBLE);
                limitAttendeeButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Please select an image for the event.", Toast.LENGTH_LONG).show();
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

        // pop up limit attendee dialog
        limitAttendeeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewEventActivity.this);
            builder.setTitle("Limit Attendee");
            // take user input
            inputLimit = new EditText(CreateNewEventActivity.this);
            inputLimit.setInputType(InputType.TYPE_CLASS_TEXT);
            inputLimit.setHint("Enter a limit");
            builder.setView(inputLimit);
            builder.setPositiveButton("Save", (dialog, which) -> {
                String attendeeLimit = inputLimit.getText().toString();
                // save the limit to firebase
                AddLimitToFirebase(attendeeLimit, eventId);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
        });

        // call method to open MapActivity
        eventLocation.setOnClickListener(v -> openMapActivity());

    } // onCreate closing

    /**
     * This method is used to save the limit to the firebase.
     * @param attendeeLimit the limit put on number of attendees for this event.
     * @param eventId the event with the specific Id.
     */
    private void AddLimitToFirebase(String attendeeLimit, String eventId) {
        eventsRef.document(eventId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    EventHelper event = documentSnapshot.toObject(EventHelper.class);
                    if (event != null) {
                        // if the event exists retrieve data
                        if (documentSnapshot.getData() != null) {
                            // assign the data to a compatible type variable
                            Map<String, Object> eventData = new HashMap<>(documentSnapshot.getData());
                            String limitForAttendee = (String) eventData.get("Attendee Limit");
                            if ((limitForAttendee == null) || (updateLimitMethodCalled == true)) {
                                Map<String, Object> limit = new HashMap<>();
                                limit.put("Attendee Limit", attendeeLimit);
                                db.collection("events")
                                        .document(eventId)
                                        .update(limit)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully updated db document with limit attendee field."))
                                        .addOnFailureListener(e -> {
                                            // handle fail to update event document with specific eventId
                                            Log.d(TAG, "Failed to add attendee limit field to db document.");
                                        });
                            } else {
                                // display dialog if field already exists for that eventId
                                showUpdateLimitDialog();
                            }
                        }

                    }
                });
    }

    /**
     * This method is used to display a dialog to the user if user wants to update the attendee limit,
     * when the attendee limit field in firebase already exists.
     */
    private void showUpdateLimitDialog() {
        updateLimitMethodCalled = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewEventActivity.this);
        builder.setTitle("An attendee limit already exists for this Event");
        builder.setMessage("Do you want to update limit?");
        builder.setPositiveButton("Update", (dialog, which) -> {
            // update firebase
            String updatedLimit = inputLimit.getText().toString();
            AddLimitToFirebase(updatedLimit, eventId);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // don't update dismiss
            dialog.dismiss();
        });
        builder.show();
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String filePath = "eventPosters/" + eventId; // Unique path for each event poster
        StorageReference fileRef = storageRef.child(filePath);

        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            addNewEvent(imageUrl); // Call method to create a new event with the image URL
        })).addOnFailureListener(e -> Toast.makeText(CreateNewEventActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
    }

    /**
     * This method is used to start the MapActivity when map/location button is clicked.
     */
//    private void openMapActivity() {
//        Intent intent = new Intent(CreateNewEventActivity.this, MapActivity.class);
//        startActivity(intent);
//    }

    protected void openMapActivity(){
        Intent intent = new Intent(CreateNewEventActivity.this, MapActivity.class);
        startActivityForResult(intent, LAUNCH_MAP_ACTIVITY);

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
                eventLocation.setText(locationString); //brings location from map activity to the location textbox

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


    private void addNewEvent(String imageUrl) {
        //generates random id for the event
        eventId = db.collection("events").document().getId();
        eventName = eventTitle.getText().toString();
        eventTime = startTime.getText().toString();
        eventDate = startDate.getText().toString();
        eventDescr = eventDescription.getText().toString();


        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        event = new EventHelper(eventId, eventName, locationString, locationLatitude, locationLongitude, eventTime, eventDate, eventDescr, eventPost);

        HashMap<String, Object> data = new HashMap<>();
        data.put("organizer", currentUserUID);
        data.put("id", event.getId());
        data.put("title", event.getTitle());
        data.put("location", event.getLocation());
        data.put("latitude", event.getLatitude());
        data.put("longitude", event.getLongitude());
        data.put("time", event.getTime());
        data.put("date", event.getDate());
        data.put("description", event.getDescription());
        data.put("poster", imageUrl);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                            String token = task.getResult().toString();
                            data.put("organizer_token", token);
                        }

//                        if (eventPost != null) {
////                            data.put("poster", eventPost);
//                            data.put("poster", imageUrl);
//                        } else {
////                            data.put("poster", "");
//                            data.put("poster", "");
//                        }
                        List<Map<String, Object>> emptySignUpList = new ArrayList<>();
                        data.put("signed_up", emptySignUpList);

                        CollectionReference eventsRef = db.collection("events");
                        eventsRef.document(eventId).set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FireStore", "DocumentSnapshot successfully written!");
                                    updateUserEvents(currentUserUID, event.getId());
                                    Toast.makeText(CreateNewEventActivity.this, "Create New Event Successful", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error writing document", e));

                    }
                });

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

    /**
     * This method is used to upload the event poster to fireStore storage,
     * and add the poster uri file path to the db.
     * @param eventId the event newly created with specific eventId
     * @param posterUri the event poster
     */
//    private void UploadPosterToFirebaseStorage(String eventId, Uri posterUri) {
//        // upload the image file path instead of the image itself
//        // initialize firebase storage and its reference
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageReference = storage.getReference();
//        // create a unique file path in the firebase storage to upload the poster
//        String eventPosterPath = "eventPosters/" + UUID.randomUUID().toString();
//        StorageReference eventPosterRef = storageReference.child(eventPosterPath);
//        // upload the path to the uri of the poster
//        UploadTask uploadTask = eventPosterRef.putFile(posterUri);
//        // listen to upload
//        uploadTask.addOnSuccessListener(taskSnapshot -> {
//            // get the url of the uploaded poster
//            eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                // get the url string
//                String posterString = uri.toString();
//                // add to db
//                Map<String, Object> Poster = new HashMap<>();
//                Poster.put("poster", posterString);
//                db.collection("events")
//                        .document(eventId)
//                        .update(Poster)
//                        .addOnSuccessListener(aVoid -> Log.d(TAG, "event document successfully updated with poster field."))
//                        .addOnFailureListener(e -> Log.d(TAG, "event document failed to update with poster field."));
//            });
//        }).addOnFailureListener(e -> {
//            Log.d(TAG,"Failed to upload poster to firebase storage");
//        });
//    } // UploadPosterToFirebaseStorage method closing

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

//    /**
//     * This method selects an image for the event poster and saves the uri to a variable.
//     */
//    private void imageChooser() {
//        Intent i = new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);
//        launchSomeActivity.launch(i);
//    }
//
//    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    Intent data = result.getData();
//                    if (data != null && data.getData() != null) {
//                        selectedImageUri = data.getData();
//                        // upload to firebase storage
//                        UploadPosterToFirebaseStorage(eventId, selectedImageUri);
//                        // display into imageview
//                        Glide.with(CreateNewEventActivity.this).load(selectedImageUri).into(uploadPoster);
//                    }
//                }
//            });

} // closing CreateNewEventActivity