//https://stackoverflow.com/a/50236950
//https://stackoverflow.com/a/26880148
//https://www.youtube.com/watch?v=qY-xFxZ7HKY
//https://stackoverflow.com/a/4981063
package com.android.quemeful_qr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This is an activity class used to handle the view for the user after scanning an event QR code.
 */
public class ViewEventActivity extends AppCompatActivity implements LocationListener{
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
    private Date currentTime;

    private CollectionReference eventsRef;
    LocationManager locationManager;
    String attendeeLocation;
    Double attendeeLatitude;
    Double attendeeLongitude;
    Map<String,Object> locationMap;

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
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(ViewEventActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ViewEventActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        
        confirmCheckInButton = findViewById(R.id.confirm_check_in_button);
        cancelButton = findViewById(R.id.cancel_button);

        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        // initialize instances from xml
        TextView eventName = findViewById(R.id.event_name_textview);
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

        assert event != null;
        poster = event.getPoster();
        title = event.getTitle();
        eventId = event.getId();
        eventName.setText(title);
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
                               DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
                               String date = df.format(Calendar.getInstance().getTime());
                               Log.d("date", date);
                               getLocation();
//                               Log.d("location", attendeeLocation);
//                               Log.d("latitude", attendeeLatitude.toString());
//                               Log.d("longitude", attendeeLongitude.toString());


                               for (int i = 0; i < signupList.size(); i++){
                                   if (signupList.get(i).get("uid").toString().equals(currentUserUID)){
                                       saveUID = signupList.get(i).get("uid").toString();
                                       checkInString = signupList.get(i).get("checked_in").toString();
                                       int checkInInt = Integer.parseInt(checkInString);
                                       signupList.get(i).replace("checked_in",String.valueOf(checkInInt+1));
                                       if (signupList.get(i).get("date_time") != null){
                                           signupList.get(i).replace("date_time", date);
                                       }else{
                                           signupList.get(i).put("date_time", date);
                                       }
                                   }
                               }

                               eventsDocRef.update("signed_up", signupList)
                                       .addOnCompleteListener(aVoid -> {
                                   // Update UI to reflect that the user has signed up
                                   Toast.makeText(ViewEventActivity.this, "Added check in event successfully!", Toast.LENGTH_SHORT).show();

                               }); //replaces the old sign up list with the new one




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


    }//closing onCreate
    @SuppressLint("MissingPermission")
    private void getLocation(){
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, ViewEventActivity.this);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this,""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        Geocoder geocoder = new Geocoder(ViewEventActivity.this, Locale.getDefault());

        geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 10, new Geocoder.GeocodeListener() {
            @Override
            public void onGeocode(List<Address> addresses) {
                for (int i = 0; i < addresses.size(); i++){
                    Log.d("address", addresses.get(i).toString());
                }
                String address = addresses.get(0).getAddressLine(0);

                attendeeLocation = address;
                attendeeLatitude = location.getLatitude();
                attendeeLongitude = location.getLongitude();


            }

        });

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }



}







    // activity class closing


