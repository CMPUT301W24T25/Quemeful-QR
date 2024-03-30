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
 * <a href="https://stackoverflow.com/questions/49382675/how-to-add-values-to-firebase-firestore-without-overwriting">...</a>
 * Author- thaffe, License- CC BY-SA 3.0, Published Date- 20 Mar, 2018, Accessed Date- 26 Mar, 2024
 */

public class GeneratePromotionalQRCodeActivity extends AppCompatActivity {

    private Bitmap bitmap;

    // fireStore firebase
    private FirebaseFirestore db;
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_promotional_qrcode);

        // declare and initialize instances from xml
        ImageView promotionQRCode = findViewById(R.id.promotion_qrcode);
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

        // get the eventId for the event passed via intent by EventPromotionActivity
        // for eventId and the event details for that event
        String eventId = getIntent().getStringExtra("eventId");
        bitmap = createBitmap(eventId);
        if (bitmap != null) {
            promotionQRCode.setImageBitmap(bitmap);
            UploadUriToFirebaseEvents(bitmap, eventId);
        } else {
            Log.d(TAG, "bitmap is still NULL");
        }

        // share button click listener
        shareButton.setOnClickListener(v -> {
            // pass the bitmap created to ShareQRCodeActivity to share
            Intent passIntent = new Intent(GeneratePromotionalQRCodeActivity.this, ShareQRCodeActivity.class);
            passIntent.putExtra("promoQRCode", bitmap);
            startActivity(passIntent);
        });
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
        Bitmap map = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        map.setPixels(dim, 0, w,0, 0, w, h);
        return map;
    }

    /**
     * This method is used to upload and add the promotional event QR Code to the firebase,
     * events collection along with its other fields.
     * @param bitmap The bitmap to be uploaded/added.
     * @param eventId The event to which the uri to be added identified by the specific eventId.
     */
    private void UploadUriToFirebaseEvents(Bitmap bitmap, String eventId) {

        // convert bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

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
                        .update(eventPromoData)
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