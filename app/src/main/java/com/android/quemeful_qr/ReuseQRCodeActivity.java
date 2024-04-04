package com.android.quemeful_qr;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity is used to display the QR code reused and the event title for which it is reused,
 * to allow scanning for checking in into the event.
 */
public class ReuseQRCodeActivity extends AppCompatActivity {

    String eventId;
    private FirebaseFirestore db;
    TextView eventTitle;
    ImageView showReusedQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reuse_qrcode);

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });

        // initialize firebase db
        db = FirebaseFirestore.getInstance();

        // initialize xml
        eventTitle = findViewById(R.id.reuse_qr_eventTitle);
        showReusedQRCode = findViewById(R.id.reuse_qr_codeDisplay);

        // get event Id to fetch the event name
        eventId = getIntent().getStringExtra("event id");
        // fetch event title and display it
        fetchEventName(eventId);

        // get the selected QR code uri to be reused for this event with the passed eventId
        String reusedUri = getIntent().getStringExtra("Selected QR uri for reuse");
        // display the QR code in the imageView to enable scanning to checkIn
        Glide.with(this).load(reusedUri).into(showReusedQRCode);

    }

    /**
     * This method is used to fetch the event title from firebase and display it in the textview.
     * @param eventId the event associated with the reused QR code
     */
    private void fetchEventName(String eventId) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        EventHelper event = documentSnapshot.toObject(EventHelper.class);
                        if (event != null) {
                            if(documentSnapshot.getData() != null) {
                                Map<String, Object> eventData = new HashMap<>(documentSnapshot.getData());
                                // get event title
                                String name = (String) eventData.get("title");
                                if(name != null) {
                                    // display the title in the text view
                                    eventTitle.setText(name);
                                } else {
                                    Log.d(TAG, "event title not found");
                                }
                            }
                        }
                    } else {
                        // when event is missing
                        Toast.makeText(getBaseContext(), "event not found", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    // handle when document does not exist - throw exception
                });
    }

} // activity closing