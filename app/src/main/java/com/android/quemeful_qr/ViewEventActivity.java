package com.android.quemeful_qr;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an activity class used to handle the view for the user after scanning an event QR code.
 */
public class ViewEventActivity extends AppCompatActivity {
    private ImageView posterView;
    private TextView textview_EventName;
    private Button confirmCheckInButton;
    private Button cancelButton;
    private String poster;
    private String title;
    private String eventId;

    /**
     * This onCreate method is used to create the view that appears after scanning a QR code.
     * It displays the event title and its poster.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        textview_EventName = findViewById(R.id.textview_event_name);
        posterView = findViewById(R.id.poster_view);
        confirmCheckInButton = findViewById(R.id.confirm_check_in_button);
        cancelButton = findViewById(R.id.cancel_button);

        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");

        assert event != null;
        poster = event.getPoster();
        title = event.getTitle();
        eventId = event.getId();
        textview_EventName.setText(title);
        assert poster != null;
        byte[] imageAsBytes = Base64.decode(poster.getBytes(), Base64.DEFAULT);
        posterView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        confirmCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }



} // activity class closing


