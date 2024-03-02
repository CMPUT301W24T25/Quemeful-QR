//https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
//https://www.youtube.com/watch?v=CQ5qcJetYAI
//https://www.youtube.com/watch?v=_mo0vPfOaAQ
//https://stackoverflow.com/questions/49831751/get-base64-string-from-image-uri
//https://firebase.google.com/docs/firestore/query-data/queries#java
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.quemeful_qr.FirebaseActivity;
import com.android.quemeful_qr.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class AddEventActivity extends AppCompatActivity {
    private ImageView backIcon;
    private ImageView menuIcon;
    private TextView title;
    private Button addposter;
    private Button chooseimage;
    private ImageView previewposter;
    private EditText addeventedittext;
    private Button addeventbutton;
    private String eventUUID;
    private Button firebasepagebutton;
    private ImageView imageviewqrcode;
    private Button uploadPic;

    private Uri selectedImageUri;
    private FirebaseFirestore db;

    private CollectionReference eventsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        setUpToolbars();

        // handle the Choose Image button to trigger
        // the image chooser function

        chooseimage = findViewById(R.id.chooseimage_button);

        previewposter = findViewById(R.id.poster_preview);
        addeventbutton = findViewById(R.id.add_event_button);
        addeventedittext = findViewById(R.id.add_event_edit_text);
        imageviewqrcode = findViewById(R.id.qr_code);
        firebasepagebutton = findViewById(R.id.firebasepage_button);
        uploadPic = findViewById(R.id.upload_pic);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        chooseimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        addeventbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventUUID = UUID.randomUUID().toString();
                String eventName = addeventedittext.getText().toString();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // In case you want to compress your image, here it's at 40%
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    Event event = new Event(eventUUID, eventName, Base64.encodeToString(byteArray, Base64.DEFAULT));
                    addNewEvent(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }






            }
        });
        firebasepagebutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openFirebaseActivity();
            }
        });

    }
    // this function is triggered when
    // the Select Image Button is clicked
    
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
                        previewposter.setImageURI(selectedImageUri);

                    }
                }
            });



    private void addNewEvent(Event event) {
// Add the event to the Firestore collection

        String eventName = addeventedittext.getText().toString();
        if (eventUUID.matches("") || eventName.matches("")){ //empty string
            Toast myToast = Toast.makeText(AddEventActivity.this, "please enter an event", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put("Event UUID", event.getEventUUID());
            data.put("Event Name", event.getEventName());
            data.put("Event Date", "14-03-2024");
            data.put("Event Poster", event.getEventPoster());
            eventsRef
                    .document(db.collection("events").document().getId())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "DocumentSnapshot successfully written!");
                            Log.d("UUID", "the UUID is " + event.getEventUUID());
                        }
                    });
            addeventedittext.setText(""); //clears the editText for next entry
            addeventedittext.requestFocus(); // sets cursor back to addCityEditText
        }
    }

    protected void openFirebaseActivity(){
        Intent intent = new Intent(AddEventActivity.this, FirebaseActivity.class);
        startActivity(intent);
    }
    protected void setUpToolbars(){
        backIcon = findViewById(R.id.backarrow_icon);
        menuIcon = findViewById(R.id.menu_icon);
        title = findViewById(R.id.toolbar_title);
        backIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openMainActivity();
                Toast mytoast = Toast.makeText(AddEventActivity.this, "back icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(AddEventActivity.this, "menu icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });

    }
    protected void openMainActivity(){
        Intent intent = new Intent(AddEventActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}