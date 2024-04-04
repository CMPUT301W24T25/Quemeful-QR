//https://stackoverflow.com/a/50236950
package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
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
    private String saveUID;
    private String checkInString;
    private FirebaseFirestore db;

    private CollectionReference eventsRef;

    /**
     * This onCreate method is used to create the view that appears after scanning a QR code.
     * It displays the event title and its poster.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        textview_EventName = findViewById(R.id.textview_event_name);
        posterView = findViewById(R.id.poster_view);
        confirmCheckInButton = findViewById(R.id.confirm_check_in_button);
        cancelButton = findViewById(R.id.cancel_button);

        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

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
                DocumentReference eventsDocRef = db.collection("events").document(eventId);
                String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//                String currentUserUID = "d06f09a667154775";
//                String currentUserUID = "439cd80ba5572b17";

                eventsDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               DocumentSnapshot document = task.getResult();
                               List<Map<String,Object>> signupList = (List<Map<String,Object>>) document.get("signed_up");


                               for (int i = 0; i < signupList.size(); i++){
                                   if (signupList.get(i).get("uid").toString().equals(currentUserUID)){
                                       saveUID = signupList.get(i).get("uid").toString();
                                       checkInString = signupList.get(i).get("checked_in").toString();
                                       int checkInInt = Integer.parseInt(checkInString);
                                       signupList.get(i).replace("checked_in",String.valueOf(checkInInt+1));
                                       }

                               }
                               eventsDocRef.update("signed_up", signupList)
                                       .addOnCompleteListener(aVoid -> {
                                   // Update UI to reflect that the user has signed up
                                   Toast.makeText(ViewEventActivity.this, "Added check in event successfully!", Toast.LENGTH_SHORT).show();

                               }); //replaces the old sign up list with the new one

//


//

//                                      eventsDocRef.update("signed_up", FieldValue.arrayUnion(signupList.get(i)))
//                                               .addOnCompleteListener(aVoid -> {
//                                                   // Update UI to reflect that the user has signed up
//                                                   Toast.makeText(ViewEventActivity.this, "Added check in event successfully!", Toast.LENGTH_SHORT).show();
//
//                                                }); //updates the signed_up list




//                               for (int j = 0; j < signupList.size(); j++){
//                Log.d("work uid", signupList.get(j).get("uid").toString());
//                Log.d("work checkin list", signupList.get(j).get("checked_in").toString());
//            }
//                               eventsDocRef.update("signed_up", FieldValue.arrayUnion(signupList))
//                                       .addOnCompleteListener(aVoid -> {
//                                           // Update UI to reflect that the user has signed up
//                                           Toast.makeText(ViewEventActivity.this, "Added check in event successfully!", Toast.LENGTH_SHORT).show();
//
//                                       }); //updates the signed_up list
//                               Toast.makeText(ViewEventActivity.this, "check-in success", Toast.LENGTH_SHORT).show();
                       }
               });


            }

        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}







    // activity class closing


