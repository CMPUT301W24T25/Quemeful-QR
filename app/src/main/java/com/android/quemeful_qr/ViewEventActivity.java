package com.android.quemeful_qr;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
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

        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");

        assert event != null;
        String poster = event.getPoster();
        String title = event.getTitle();
        String uuid = event.getId();
        textview_EventName.setText(title);
        assert poster != null;
        byte[] imageAsBytes = Base64.decode(poster.getBytes(), Base64.DEFAULT);
        posterView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

    }

//    private void signUpForEvent(String eventId) {
//        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        DocumentReference eventRef = db.collection("events").document(eventId);
//
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("uid", currentUserUID);
//        userMap.put("checked_in", "0"); // Assuming "0" means not checked-in and "1" means checked-in
//
//        eventRef.update("signed_up", FieldValue.arrayUnion(userMap))
//                .addOnSuccessListener(aVoid -> {
//                    // Update UI to reflect that the user has signed up
//                    Toast.makeText(EventDetailsActivity.this, "Signed up for event successfully!", Toast.LENGTH_SHORT).show();
//                    updateUIBasedOnUserStatus(true, false);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
//                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Handle the error
//                });
//    }

} // activity class closing
