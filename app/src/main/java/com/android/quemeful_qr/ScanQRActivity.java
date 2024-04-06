
package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is an activity class used to handle the scanning of a QR and,
 * its results, to fetch the right event data from the firebase.
 * Reference URLs:
 * https://firebase.google.com/docs/firestore/query-data/queries#java
 * Author- Firebase Documentation, License- Apache 2.0, Published Date- 14 Mar, 2024
 * https://stackoverflow.com/a/8638723/remove-leading-and-trailing-brackets-in-a-string
 * Author- James Raitsev, License- CC BY-SA 3.0, Published Date- 26 Dec, 2011
 */
public class ScanQRActivity extends AppCompatActivity {
    //scan and generate QR
    private ImageButton scan;
    private TextView confirm;
    private FirebaseFirestore db;

    private CollectionReference eventsRef;

    private String eventPoster;
    private String eventName;
    private String eventUUID;
    private String eventTime;
    private String eventDate;
    private String eventDescription;


    /**
     * The onCreate method of this activity is used to set a listener on the camera icon button to scan a QR code.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        scan = findViewById(R.id.buttonCam);
        confirm = findViewById(R.id.camera_frame);

        // navigates back to the previous page on clicking the back arrow
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back clicked
                finish();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create object of IntentIntegrator class of the QR library
                IntentIntegrator integrator = new IntentIntegrator(ScanQRActivity.this);
                integrator.setPrompt("Scan a QR code");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });

    }

    /**
     * This method handles the result of the scan of a QR code.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if (result.getContents() == null) {
                Toast.makeText(getBaseContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
            else{
                confirm.setText(result.getContents());
                Toast.makeText(getBaseContext(), "Scanned successfully", Toast.LENGTH_SHORT).show();
                // check if a document exists with the id and name we scanned
                //if exists, display it
                //if not exist, error message
                db.collection("events")
                        .whereEqualTo("id", result
                                .getContents()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                            /**
                             * This method is used to compare id from QR code with the ids in firebase,
                             * and if finds a match, then fetches the data and switch to next page.
                             * @param task the task to be done on complete.
                             */
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) { //for every document found (loop runs once - only 1 document matches uuid)
                                        Log.d(TAG, document.getId() + "=>>" + document.getData().values());
                                        //brings the user to a new activity with event details
                                        eventUUID = result.getContents();
                                        eventName = document.getData().get("title").toString();
                                        eventPoster = document.getData().get("poster").toString();
                                        eventTime = document.getData().get("time").toString();
                                        eventDate = document.getData().get("date").toString();
                                        eventDescription = document.getData().get("description").toString();
                                        confirm.setText(eventPoster);

                                        EventHelper event = new EventHelper(eventUUID, eventName, "location", eventTime, eventDate, eventDescription, eventPoster);
                                        Intent intent = new Intent(ScanQRActivity.this, ViewEventActivity.class);

                                        intent.putExtra("event", event);
                                        startActivity(intent);

                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    confirm.setText("QR code not recognized");
                                    //set the error message onto the camera textview "QR code not recognized"
                                }
                            }
                        });
            }

        }
        else{
            //pass the result to the activity
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

} // activity class closing
