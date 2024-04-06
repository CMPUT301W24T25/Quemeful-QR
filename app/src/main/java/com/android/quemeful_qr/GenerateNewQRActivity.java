package com.android.quemeful_qr;

import static android.graphics.Color.BLACK; // black qr code
import static android.graphics.Color.WHITE; // white background
import static android.service.controls.ControlsProviderService.TAG;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat; // import for library used to generate QR code
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an activity class for generating QR code and make it return the QR code uri.
 * Note: For the purpose of generating and scanning a QR code,
 * zxing library has been implemented in the build.gradle.kts(Module:app)
 */
public class GenerateNewQRActivity extends AppCompatActivity {
    private Bitmap bitmap;

    // fireStore firebase
    private FirebaseFirestore db;
    private StorageReference reference;

    /**
     * This onCreate method is used to setOnClickListener to the generate button and displays the QR code as image.
     * The generation and appearance of the QR code is done in the createBitmap method below.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_new_qractivity);
        // initialize the xml variables
        TextView eventTitle = findViewById(R.id.eventName_text);
        ImageView QRImage = findViewById(R.id.QRImage);
        // initialize the fireStore
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        // get the eventId and name passed
        String eventId = getIntent().getStringExtra("eventId");
        String title = getIntent().getStringExtra("event name");
        // create the bitmap using the eventId
        bitmap = createBitmap(eventId);
        if (bitmap != null) {
            QRImage.setImageBitmap(bitmap); // display the bitmap (QR code)
            UploadToFirebase(bitmap, eventId); // add to the event document
        } else {
            Log.d(TAG, "bitmap for check in QR code is NULL"); // bitmap null condition
        }
        if(title != null){
            eventTitle.setText(title);
        }

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
//            // back clicked go to dashboard
//            Intent intent = new Intent(GenerateNewQRActivity.this, MainActivity.class);
//            startActivity(intent);
            finish();
        });

    } // onCreate closing

    /**
     * This method is used to create/Encode the QR code using BitMatrix.
     * References: URLs (Access Date: 21.02.2024)
     * <a href="https://www.youtube.com/watch?v=zHStZwXtbj0">...</a>
     * Author- Programmity  , Published Date- Nov 17, 2020
     * <a href="https://www.youtube.com/watch?v=5DEHmN4PmA0">...</a>
     * Author- GeeksforGeeks  , Published Date- Jul 6, 2021
     * <a href="https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application">...</a>
     * Author- Enamul Haque , License- CC BY-SA 3.0  , Published Date- Jan 28, 2018
     * <a href="https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code">...</a>
     * Author- Harpreet , License- CC BY-SA 4.0 , Published Date- Jan 16, 2019
     * @param text Takes string input to create a QR code based on that
     * @return Returns BitMap map
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
     * This method is used to get the uri of the promotion event QR Code image and return it using the UploadListener.
     * @param bitmap The bitmap (promotion QR Code image) to upload.
     * @param eventId the event with specific eventId
     */
    private void UploadToFirebase(Bitmap bitmap, String eventId) {

        // convert bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

        // a unique path (folder) in the firebase storage to upload the check in Qr images
        String QrImagePath = "CheckInQRCodeImages/" + System.currentTimeMillis() + ".jpg";
        StorageReference QrImageRef = reference.child(QrImagePath);

        // upload
        UploadTask uploadTask = QrImageRef.putBytes(imageByteArray);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // get download url of the QR code
            QrImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // get the uri consisting the download url to string
                String CheckInQRCodeURI = uri.toString();
                // add this uri to the specific event in the firebase
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("CheckIn QR Code", CheckInQRCodeURI);
                db.collection("events")
                        .document(eventId)
                        .update(eventData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG,"Event with specific eventId successfully updated with check in QR field.");
                        }).addOnFailureListener(e -> {
                            // handle fail to update event document with specific eventId
                            Log.d(TAG, "failed to add event check in QR Code field to events collection with document eventId.");
                        });

            }).addOnFailureListener(e -> {
                // handle fail to download url
            });
        }).addOnFailureListener(e -> {
            // handle upload to storage failure
        });
    }

} // activity class closing