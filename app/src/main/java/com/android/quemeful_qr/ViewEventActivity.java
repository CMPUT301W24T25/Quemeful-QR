package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * This is an activity class used to handle the view for the user after scanning an event QR code.
 */
public class ViewEventActivity extends AppCompatActivity {

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

        // initialize instances from xml
        TextView eventName = findViewById(R.id.textview_event_name);
        ImageView eventPoster = findViewById(R.id.poster_view);
        TextView eventDesc = findViewById(R.id.textview_event_description);
        TextView eventDate = findViewById(R.id.textview_event_date);
        TextView eventTime = findViewById(R.id.textview_event_time);

        // clicking on the back arrow on top navigates back to the previous page
        Toolbar toolbar = (Toolbar) findViewById(R.id.backTool);
        toolbar.setNavigationOnClickListener(v -> {
            // back clicked - close this activity
            finish();
        });

        // get the event passed by ScanQRActivity result via intent
        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");

        if (event != null) {
            String title = event.getTitle();
            eventName.setText(title);
            String poster = event.getPoster();
            if(poster != null){
                byte[] imageAsBytes = Base64.decode(poster.getBytes(), Base64.DEFAULT);
                eventPoster.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            }
            String desc = event.getDescription();
            eventDesc.setText(desc);
            String date = event.getDate();
            eventDate.setText(date);
            String time = event.getTime();
            eventTime.setText(time);

        }

    }

} // activity class closing
