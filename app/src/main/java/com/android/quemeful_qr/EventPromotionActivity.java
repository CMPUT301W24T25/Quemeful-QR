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

        // get eventId for the event promotion
//        String eventId = getIntent().getStringExtra("eventId");
        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");
        assert event != null; // to prevent null pointer exception
        String eventId = event.getId(); // eventId getter method from EventHelper class.
        if (eventId != null) {
            fetchEventDetails(eventId); // fetch event details using the id
        } else {
            // when eventId is null or missing
            Toast.makeText(getBaseContext(), "eventId does not exist", Toast.LENGTH_SHORT).show();
        }

    } // onCreate method closing

    /**
     * This method is used to fetch the event details from the firebase.
     * (purpose - to check if promotions for that event is available)
     */
    private void fetchEventDetails(String eventId) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        EventHelper event = documentSnapshot.toObject(EventHelper.class);
                        if (event != null) {
                            Map<String, Object> eventData = new HashMap<>(documentSnapshot.getData());
                            // Check if the event has promotion or not
                            if (!Boolean.TRUE.equals(eventData.get("promotion"))) {
                                updateUIPromotionAvailable(); // yes - show promotion QR code and share
                            }
                            else {
                                updateUIPromotionUnavailable(); // no - show generate promotion Qr code
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
     */
    private void updateUIPromotionAvailable() {
        promotionQRCode.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        noPromotionMessage.setVisibility(View.GONE);
        generatePromotionQR.setVisibility(View.GONE);

        shareButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventPromotionActivity.this, ShareQRCodeActivity.class);
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
            startActivity(intent);
        });
    }

} // activity class closing