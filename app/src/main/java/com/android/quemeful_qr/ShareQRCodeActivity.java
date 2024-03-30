package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * This is an activity class is used to share the generated event promotional QR Code.
 * Reference URLS:
 * <a href="https://stackoverflow.com/questions/60511988/android-method-compress-must-be-called-from-worker-thread">...</a>
 * Author- Ryan M, License- CC BY-SA 4.0, Published Date- 3 Mar, 2020, Accessed Date- 20 Mar, 2024
 * <a href="https://www.youtube.com/watch?v=Dmc5aF3bLWI">...</a>
 * Author- Programmer World, Published Date- 13 Jan, 2023, Accessed Date- 21 Mar, 2024
 * <a href="https://developer.android.com/guide/components/processes-and-threads#WorkerThreads">...</a>
 * Author- Developers Android, License- Apache 2.0, Published Date- 03 Jan, 2024, Accessed Date- 22 Mar, 2024
 * <a href="https://stackoverflow.com/questions/3351553/how-to-show-an-activity-as-pop-up-on-other-activity">...</a>
 * Author- oli, License- CC BY-SA 2.5, Published Date- 28 Jul, 2010, Accessed Date- 26 Mar, 2024
 * <a href="https://developer.android.com/training/sharing/send">...</a>
 * Author- Developer Android, License- Apache 2.0, Published Date- 20 Mar, 2024, Accessed Date- 26 Mar, 2024
 * <a href="https://stackoverflow.com/questions/67327522/sharing-a-url-with-image-preview-data-sharesheet">...</a>
 * Author- Hieu nguyen van, License- CC BY-SA 4.0, Published Date- 28 May, 2021, Accessed Date- 26 Mar, 2024
 */

public class ShareQRCodeActivity extends AppCompatActivity  {

    private byte[] imageByteArray; // for bitmap to byte array conversion
    private Bitmap passedBitmap; // generated new promo qr code bitmap
    private Uri passedUri; // uri of existing promo qr code

    // firebase
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fireStore storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        // get the bitmap passed by GeneratePromotionalQRCodeActivity
        passedBitmap = getIntent().getParcelableExtra("promoQRCode");

        // get the uri string passed by EventPromotionActivity
        String passedUriString = getIntent().getStringExtra("existing promo QR Code uri");
        // convert uri string to Uri
        if(passedUriString != null) {
                passedUri = Uri.parse(passedUriString);
        } else {
            Log.d(TAG, "Error in getting the existing promo uri");
        }

        UriToShare(); // call the share method
    }

    /**
     * This method is used to share the event promo QR code that on scanning shows the event details.
     */
    private void UriToShare(){
        // get the string url after uploading the QR code to firebase storage using the interface defined below.
        if (passedBitmap != null) {
            // for generate new promo QR code
            getDownloadUrl(passedBitmap, promo_qr_url -> {
                // convert url string to uri
                Uri uri = Uri.parse(promo_qr_url);
                // intent to share
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setClipData(ClipData.newRawUri("event promotional QR Code", uri));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/jpg");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // give permission
                // show the android share sheet/ share pop up
                startActivity(Intent.createChooser(intent, "Share Using"));
            });

        } if (passedUri != null){ // for existing promo QR code
            Intent intent = new Intent(Intent.ACTION_SEND); // intent to share
            intent.setClipData(ClipData.newRawUri("event promotional QR Code", passedUri));
            intent.putExtra(Intent.EXTRA_STREAM, passedUri);
            intent.setType("image/jpg");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // give permission
            startActivity(Intent.createChooser(intent, "Share Using"));
        }
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
     * This interface is used as a listener for method getDownloadUrl.
     * purpose - to getDownloadUrl() as return, as required to get the uri to be shared.
     */
    public interface UploadListener {
        void onUpload(String promo_qr_url);
    }

    /**
     * This method is used to get the uri of the promotion event QR Code image and return it using the UploadListener.
     * @param bitmap The bitmap (promotion QR Code image) to upload.
     * @param uploadListener used to get the download url as a return after upload task.
     */
    private void getDownloadUrl(Bitmap bitmap, UploadListener uploadListener) {
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
                uploadListener.onUpload(promoQRCodeURI);

            }).addOnFailureListener(e -> {
                // handle fail to download url
            });
        }).addOnFailureListener(e -> {
            // handle upload to storage failure
        });
    }

} // activity closing