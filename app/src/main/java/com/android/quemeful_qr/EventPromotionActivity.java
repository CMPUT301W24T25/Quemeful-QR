package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an activity class that handles the event promotions requirement.
 * Reference xml bug fix url - <a href="https://stackoverflow.com/questions/71151389/background-is-not-showing-up-on-android-studio-button">...</a>
 * Author- GHH, License- CC BY-SA 4.0, Published Date- 17 Feb, 2022, Accessed Date- 20 Mar, 2024
 */

public class EventPromotionActivity extends AppCompatActivity {
    private AppCompatButton generatePromotionQR;
    private TextView noPromotionMessage, shareButton;
    private ImageView promotionQRCode;
    private String eventId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_promotion);

        generatePromotionQR = findViewById(R.id.QR_generate_button_for_eventPromotion);
        noPromotionMessage = findViewById(R.id.promotionMessage);
        shareButton = findViewById(R.id.share_button);
        promotionQRCode = findViewById(R.id.promotion_qrcode);

        db = FirebaseFirestore.getInstance();

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });

        // get eventId passed via intent by EventDetailsActivity
        eventId = getIntent().getStringExtra("eventId");
        fetchEventDetails(eventId);

    } // onCreate method closing

    /**
     * This method is used to fetch the event details from the firebase.
     * (purpose - to check whether promotions for that event is available or not)
     */
    private void fetchEventDetails(String eventId) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        EventHelper event = documentSnapshot.toObject(EventHelper.class);
                        if (event != null) {
                            if(documentSnapshot.getData() != null) {
                                Map<String, Object> eventData = new HashMap<>(documentSnapshot.getData());
                                // Check if the event has promotion or not
                                String eventPromo = (String) eventData.get("Event Promotion QR Code");
                                if (eventPromo != null) {
                                    updateUIPromotionAvailable(eventPromo); // yes - show promotion QR code and share
                                } else {
                                    updateUIPromotionUnavailable(); // no - show generate promotion Qr code
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

    /**
     * This method is used to change the UI when promotion for that event is available.
     * And, the promo QR code is shareable.
     * Reference- <a href="https://stackoverflow.com/questions/41737271/how-to-display-or-get-an-image-from-firebase-storage">...</a>
     * Author-Diego Venancio, License- CC BY-SA 3.0, Published Date- 30 Aug, 2017, Accessed Date- 24 Mar, 2024
     * @param uri The promo QR code image uri
     */
    private void updateUIPromotionAvailable(String uri) {
        promotionQRCode.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        noPromotionMessage.setVisibility(View.GONE);
        generatePromotionQR.setVisibility(View.GONE);

        // load the url to imageview
        Glide.with(EventPromotionActivity.this)
             .load(uri)
             .into(promotionQRCode);

        shareButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventPromotionActivity.this, ShareQRCodeActivity.class);
            // pass the uri via intent to share activity
            intent.putExtra("existing promo QR Code uri", uri);
            startActivity(intent);
        });
    }

    /**
     * This method is used to change the UI when promotion for that event is not available.
     * And, sets a click listener on the generate new button to generate a new promotional Qr code.
     */
    private void updateUIPromotionUnavailable() {
        promotionQRCode.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);
        noPromotionMessage.setVisibility(View.VISIBLE);
        generatePromotionQR.setVisibility(View.VISIBLE);

        generatePromotionQR.setOnClickListener(v -> {
            Intent intent = new Intent(EventPromotionActivity.this, GeneratePromotionalQRCodeActivity.class);
            // pass the eventId to generate new promotional QR code
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });
    }

} // activity class closing