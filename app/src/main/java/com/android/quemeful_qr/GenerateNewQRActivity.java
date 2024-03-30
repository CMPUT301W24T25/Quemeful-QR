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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat; // import for library used to generate QR code
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

/**
 * This is an activity class for generating QR code and make it return the QR code uri.
 * Note: For the purpose of generating and scanning a QR code,
 * zxing library has been implemented in the build.gradle.kts(Module:app)
 */
public class GenerateNewQRActivity extends AppCompatActivity {
    private ImageView QRImage;
    private TextView eventTitle;
    private String eventId, uri;
    private byte[] imageByteArray; // for bitmap to byte array conversion
    private Bitmap bitmap;

    // fireStore firebase
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
        eventTitle = findViewById(R.id.eventName_text);
        QRImage = findViewById(R.id.QRImage);
        // initialize the fireStore
        FirebaseStorage storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        // get the event passed
        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");
        if (event != null) {
            eventTitle.setText(event.getTitle()); // get event name and set text
            eventId = event.getId(); // get eventId
        } else {
            Log.d(TAG,"event is NULL" ); // error handle for if event is not received.
        }
        // create the bitmap using the eventId
        bitmap = createBitmap(eventId);
        if (bitmap != null) {
            QRImage.setImageBitmap(bitmap); // display the bitmap (QR code)
        } else {
            Log.d(TAG, "bitmap for check in QR code is NULL"); // bitmap null condition
        }

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked
            finish();
        });

        // pass the uri string of the generated check in Qr code to be used to store it in the db.
        String checkInUri = getCheckInQRCodeUri();
        Intent intent1 = new Intent(GenerateNewQRActivity.this, CreateNewEventActivity.class);
        intent1.putExtra("CheckIn QR code uri string", checkInUri);
        startActivity(intent1);
    }

    /**
     * This method is used in create new event activity as a getter method for the uri string,
     * of the generated QR code used for check in.
     * @return uri of the generated check in QR code.
     */
    private String getCheckInQRCodeUri(){
        getDownloadUrl(bitmap, checkIn_qr_url -> {
            uri = checkIn_qr_url;
        });
        return uri;
    }

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
     * This interface is used as a listener for method getDownloadUrl.
     * purpose - to getDownloadUrl() as return, as required to get the uri to other methods.
     */
    public interface UploadUriListener {
        void onUpload(String checkIn_qr_url);
    }

    /**
     * This method is used to convert the bitmap to byte array in a worker thread.
     * @param bitmap  The bitmap to be converted to byte array.
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
     * This method is used to get the uri of the promotion event QR Code image and return it using the UploadListener.
     * @param bitmap The bitmap (promotion QR Code image) to upload.
     * @param uploadUriListener used to get the download url as a return after upload task.
     */
    private void getDownloadUrl(Bitmap bitmap, UploadUriListener uploadUriListener) {
        // call method to convert bitmap to byte array
        // (done in a worker thread to prevent block in main (UI) thread
        BitmapToByteArray(bitmap);

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
                uploadUriListener.onUpload(CheckInQRCodeURI);

            }).addOnFailureListener(e -> {
                // handle fail to download url
            });
        }).addOnFailureListener(e -> {
            // handle upload to storage failure
        });
    }

} // activity class closing