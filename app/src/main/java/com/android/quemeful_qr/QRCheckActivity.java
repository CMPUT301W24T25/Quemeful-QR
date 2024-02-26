package com.android.quemeful_qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This code here is the MainActivity code of the QRScanner project done locally.
 */
public class QRCheckActivity extends AppCompatActivity {
    //scan and generate QR
    private Button generateQR;
    private Button scanQR;
    private Button createNewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcheck);

        // MainActivity code for scanning and generating QR
        generateQR = findViewById(R.id.generate_button);
        scanQR = findViewById(R.id.scan_button);
        createNewEvent = findViewById(R.id.create_new_event_button);

        // navigating back to the previous activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back clicked
                finish();
            }
        });

        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCheckActivity.this, GenerateNewQRActivity.class);
                startActivity(intent);
            }
        });

        /**
         * This method is placed here just for checking purpose.
         * Originally should be put to use in the event details page (see mockup).
         */
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCheckActivity.this, ScanQRActivity.class);
                startActivity(intent);
            }
        });

        /**
         * This method is placed here just for checking purpose.
         * Originally should to put to use in the organizers event page (see mockup)
         */
        createNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCheckActivity.this, CreateNewEventActivity.class);
                startActivity(intent);
            }
        });
    }
}