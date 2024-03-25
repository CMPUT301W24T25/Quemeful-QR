package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an activity class used to generate a new promotional QR Code for an an event and upload it to firebase.
 * Reference URLs:
 * <a href="https://stackoverflow.com/questions/29698258/convert-mapinteger-object-to-json-with-gson">...</a>
 * Author- AlexWalterbos, License- CC BY-SA 3.0, Published Date- 17 Apr, 2015, Accessed Date - 22 Mar, 2024
 * <a href="https://github.com/google/gson/releases">...</a>
 * Author- GitHub Gson, License- Apache 2.0, Published Date- 06 Jan, 2023, Accessed Date- 22 Mar, 2024
 */

public class GeneratePromotionalQRCodeActivity extends AppCompatActivity {

    // from xml
    private ImageView promotionQRCode;

    // for eventId and the event details for that event
    private String eventId, jsonEventData;
    private byte[] imageByteArray;

    // fireStore firebase
    private FirebaseFirestore db;
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_promotional_qrcode);

        // initialize instances
        promotionQRCode = findViewById(R.id.promotion_qrcode);
        TextView shareButton = findViewById(R.id.share_button);
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked - close this activity
            finish();
        });

        // get the eventId for the event
        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");
        assert event != null; // to prevent null pointer exception
        eventId = event.getId(); // eventId getter method from EventHelper class.
        if(eventId != null){
            fetchEventDetails(eventId); // fetch event details of the specific eventId.
        } else {
            Log.d(TAG, "eventId does not exist");
        }

        // share button click listener
        shareButton.setOnClickListener(v -> {
            // pass the bitmap created to ShareQRCodeActivity to share
            Intent passIntent = new Intent(GeneratePromotionalQRCodeActivity.this, ShareQRCodeActivity.class);
            // fetch the event details from firebase using event Id in string.
            String eventDetails = fetchEventDetails(eventId);
            // use the event data in string to encode the QR Code
            Bitmap bitmap = createBitmap(eventDetails);
            passIntent.putExtra("promoQRCode", bitmap);
            startActivity(passIntent);
        });
    }

    /**
     * This method is used to fetch the event details from the firebase.
     * @param eventId the event is identified with its specific Id.
     * @return string event data.
     */
    private String fetchEventDetails(String eventId) {
        db.collection("events").document(eventId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        EventHelper event = documentSnapshot.toObject(EventHelper.class);
                        if (event != null) {
                            // retrieve the event data from this document to Map
                            Map<String, Object> eventData = documentSnapshot.getData();
                            // convert map to json string to be able to encode
                            Gson gson = new Gson();
                            jsonEventData = gson.toJson(eventData);
                            // pass the json string to createBitmap() as parameter to encode it
                            Bitmap bitmap = createBitmap(jsonEventData); // our event promo bitmap (QR Code)
                            UploadUriToFirebaseEvents(bitmap, eventId);
                            promotionQRCode.setImageBitmap(bitmap); // display the bitmap (QR Code)
                        } else {
                            // when event is missing
                            Log.d(TAG, "event not found");
                        }
                    }
                }).addOnFailureListener(e -> {
                    // handle when document does not exist - throw exception
                });
        return jsonEventData;
    }

    /**
     * This method is used to create/Encode the QR code using BitMatrix.
     * @param text Takes input a String to create a QR code based on that.
     * @return Bitmap bitmap.
     */
    private Bitmap createBitmap(String text) {
        BitMatrix matrix;
        try {
            // encode the QR code
            matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 250, 250);
        }
        catch(WriterException e){
            e.printStackTrace();
            return null;
        }
        // appearance of QR Code
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] dim = new int[w * h];
        for(int i = 0; i < h ; i++){
            int offset = i * w;
            for(int j = 0; j < w; j++){
                dim[offset + j] = matrix.get(j,i) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(dim, 0, w,0, 0, w, h);
        return bitmap;
    }

    /**
     * This method is used to convert the bitmap to byte array in a worker thread.
     * @param bitmap The new bitmap generated for event promotion.
     */
    private void BitmapToByteArray(Bitmap bitmap) {
        new Thread(() -> {
            // converting bitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
            imageByteArray = byteArrayOutputStream.toByteArray();
        }).start();
    }

    /**
     * This method is used to upload the generated event promotion QR Code to that specific event in the firebase.
     * @param bitmap The new bitmap generated for event promotion.
     */
    private void UploadUriToFirebaseEvents(Bitmap bitmap, String eventId) {
        // call method to convert bitmap to byte array (done in a worker thread to prevent block in main (UI) thread
        BitmapToByteArray(bitmap);

        // a unique path (folder) in the firebase storage to upload the promo Qr images
        String QrImagePath = "PromoQRCodeImages/" + System.currentTimeMillis() + ".jpg";
        StorageReference QrImageRef = reference.child(QrImagePath);

        // upload
        UploadTask uploadTask = QrImageRef.putBytes(imageByteArray);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // get download url of the promo QR code
            QrImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // get the uri consisting the download url to string
                String promoQRCodeURI = uri.toString();

                // add this uri to the specific event in the firebase
                Map<String, Object> eventPromoData = new HashMap<>();
                eventPromoData.put("Event Promotion QR Code", promoQRCodeURI);
                db.collection("events")
                        .document(eventId)
                        .set(eventPromoData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG,"Event with specific eventId successfully updated with promotion field.");
                        }).addOnFailureListener(e -> {
                            // handle fail to update event document with specific eventId
                            Log.d(TAG, "failed to add event promotion QR Code field to events collection with document eventId.");
                        });
            }).addOnFailureListener(e -> {
                // handle fail to download url
            });
        }).addOnFailureListener(e -> {
            // handle upload to storage failure
        });
    }

} // activity class closing