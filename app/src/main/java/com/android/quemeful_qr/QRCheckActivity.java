//https://firebase.google.com/docs/firestore/query-data/queries#java
//https://stackoverflow.com/a/8638723
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
 * This code here is the MainActivity code of the QRScanner project done locally.
 */
public class QRCheckActivity extends AppCompatActivity {
    //scan and generate QR
    private ImageButton scan;
    private TextView camera;
    private FirebaseFirestore db;

    private CollectionReference eventsRef;

    private String eventPoster;
    private String eventName;
    private String eventUUID;


    /**
     * The onCreate method of this activity is used to set a listener on the camera icon button to scan a QR code.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcheck);

        scan = findViewById(R.id.buttonCam);
        camera = findViewById(R.id.camera_frame);


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
                IntentIntegrator integrator = new IntentIntegrator(QRCheckActivity.this);
                integrator.setPrompt("Scan a QR code");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });
        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                eventPoster = null;
            } else {
                eventPoster = extras.getString("key");
            }
        } else {
            eventPoster= (String) savedInstanceState.getSerializable("key");

        }
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
                camera.setText(result.getContents());
                Toast.makeText(getBaseContext(), "Scanned successfully", Toast.LENGTH_SHORT).show();
                // check if a document exists with the id and name we scanned
                //if exists, display it
                //if not exist, error message
                db.collection("events").whereEqualTo("Event UUID", result.getContents()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) { //for every document found (loop runs once - only 1 document matches uuid)
                                Log.d(TAG, document.getId() + "=>>" + document.getData().values());
                                //brings the user to a new activity with event details
                                eventUUID = result.getContents();
                                eventName = document.getData().get("Event Name").toString();
                                eventPoster = document.getData().get("Event Poster").toString();
                                camera.setText(eventPoster);

                                EventHelper event = new EventHelper(eventUUID, eventName, "location", "time", "date", "description", eventPoster);
                                Intent intent = new Intent(QRCheckActivity.this, ViewEventActivity.class);

                                intent.putExtra("event", event);
                                startActivity(intent);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            camera.setText("QR code not recognized");
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

}