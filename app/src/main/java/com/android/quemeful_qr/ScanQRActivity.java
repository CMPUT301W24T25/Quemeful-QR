package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is an activity class used to handle the scanning of a QR and,
 * its results, to fetch the right event data from the firebase.
 * Reference URLs:
 * <a href="https://firebase.google.com/docs/firestore/query-data/queries#java">...</a>
 * Author- Firebase Documentation, License- Apache 2.0, Published Date<a href="-">14 Mar, 2024
 * https://stackoverflow.com/a/8638723/remove-leading-and-trailing-br</a>ackets-in-a-string
 * Author- James Raitsev, License- CC BY-SA 3.0, Published Date- 26 Dec, 2011
 */
public class ScanQRActivity extends AppCompatActivity {
    private TextView confirm;

    private String eventPoster;
    private String eventName;
    private String eventUUID;
    private String eventTime;
    private String eventDate;
    private String eventDescription;
    private String eventLocation;
    private Double eventLatitude;
    private Double eventLongitude;

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

        //scan and generate QR
        ImageButton scan = findViewById(R.id.buttonCam);
        confirm = findViewById(R.id.camera_frame);

        // navigates back to the previous page on clicking the back arrow
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);

        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });

        scan.setOnClickListener(v -> {
            // create object of IntentIntegrator class of the QR library
            IntentIntegrator integrator = new IntentIntegrator(ScanQRActivity.this);
            integrator.setPrompt("Scan a QR code");
            integrator.setCameraId(0);
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (data != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            // Your existing code to handle the result...
            if(result != null){
                if (result.getContents() == null) {
                    Toast.makeText(getBaseContext(), "Scan Cancelled", Toast.LENGTH_LONG).show();
                }
                else{
//                    confirm.setText(result.getContents());
                    Toast.makeText(getBaseContext(), "Scanned successfully", Toast.LENGTH_LONG).show();

                    /**
                     * This method is used to compare id from QR code with the ids in firebase,
                     * and if finds a match, then fetches the data and switch to next page.
                     * @param task the task to be done on complete.
                     */
                    db.collection("events").whereEqualTo("id", result
                            .getContents()).get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //for every document found (loop runs once - only 1 document matches uuid)
                                Log.d(TAG, document.getId() + "=>>" + document.getData().values());
                                //brings the user to a new activity with event details
                                eventUUID = result.getContents();
                                eventName = document.getData().get("title").toString();
                                eventLocation = document.getData().get("location").toString();
                                eventLatitude = (Double) document.getData().get("latitude");
                                eventLongitude = (Double) document.getData().get("longitude");
                                eventPoster = document.getData().get("poster").toString();
                                eventTime = document.getData().get("time").toString();
                                eventDate = document.getData().get("date").toString();
                                eventDescription = document.getData().get("description").toString();
//                                confirm.setText(eventPoster);

                                EventHelper event = new EventHelper(eventUUID, eventName, eventLocation, eventLatitude, eventLongitude, eventTime, eventDate, eventDescription, eventPoster);
                                Intent intent = new Intent(ScanQRActivity.this, ViewEventActivity.class);

                                intent.putExtra("event", event);
                                startActivity(intent);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            confirm.setText("QR code not recognized");
                            //set the error message onto the camera textview "QR code not recognized"
                        }
                    });
                }
            } else {
                //pass the result to the activity
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);




    }

} // activity class closing