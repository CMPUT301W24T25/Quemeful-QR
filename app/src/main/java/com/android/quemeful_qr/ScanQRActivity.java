package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * This is a new activity for Scanning a QR code.
 * This code is just a check if the QR code is scanned correctly.
 * For the project, this same piece of code maybe be used to scan a QR code.
 * Note: Camera access permission required in the AndroidManifest.xml
 */

/**
 * References: URLs (Should specifically add Author, License, Published Date and Access Date: 21.02.2024)
 * https://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application
 * https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
 * for xml of this activity
 * https://stackoverflow.com/questions/21633637/rounded-corners-android-image-buttons
 */

public class ScanQRActivity extends AppCompatActivity {
    private ImageButton scan;
    private TextView camera;

    /**
     * The onCreate method of this activity is used to set a listener on the camera icon button to scan a QR code.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qractivity);

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
                IntentIntegrator integrator = new IntentIntegrator(ScanQRActivity.this);
                integrator.setPrompt("Scan a QR code");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
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

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if (result.getContents() == null) {
                Toast.makeText(getBaseContext(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
            else{
                camera.setText(result.getContents());
                Toast.makeText(getBaseContext(), "Scanned successfully", Toast.LENGTH_SHORT).show();

            }
        }
        else{
            //pass the result to the activity
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}