//https://stackoverflow.com/a/50236950
//https://stackoverflow.com/a/26880148
//https://www.youtube.com/watch?v=qY-xFxZ7HKY
//https://stackoverflow.com/a/4981063 thread
//https://stackoverflow.com/a/2227299

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
import android.widget.EditText;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private Button confirmCheckInButton;
    private Button cancelButton;
    private String poster;
    private String title;
    private String eventId;
    private String saveUID;
    private String checkInString;
    private FirebaseFirestore db;
    private TextView eventName;
    private ImageView eventPoster;
    private TextView eventDesc;
    private TextView eventDate;
    private TextView eventTime;

    private CollectionReference eventsRef;
    private LocationManager locationManager;

    private Double attendeeLatitude;
    private Double attendeeLongitude;
    private EditText addressText;



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
        addressText = findViewById(R.id.address_text);


        //initialize firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        // initialize instances from xml
        eventName = findViewById(R.id.event_name_textview);
        eventPoster = findViewById(R.id.poster_view);
        eventDesc = findViewById(R.id.textview_event_description);
        eventDate = findViewById(R.id.textview_event_date);
        eventTime = findViewById(R.id.textview_event_time);


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
                DocumentReference user = db.collection("users").document(currentUserUID);
                Map<String, Object> locationMap = getLocation();
                eventsDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               DocumentSnapshot document = task.getResult();
                               List<Map<String,Object>> signupList = (List<Map<String,Object>>) document.get("signed_up");
                               DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
                               String date = df.format(Calendar.getInstance().getTime());
                               Log.d("date", date);

                               Log.d("latitude", locationMap.get("attendee_latitude").toString());
                               Log.d("longitude", locationMap.get("attendee_longitude").toString());
                               attendeeLatitude = (Double) locationMap.get("attendee_latitude");
                               attendeeLongitude = (Double) locationMap.get("attendee_longitude");

                               Log.d("aLatitude", attendeeLatitude.toString());


                               boolean attendeeFound = false;
                               for (int i = 0; i < signupList.size(); i++){ //looks through sign up list to see if the user has already signed in

                                   if (signupList.get(i).get("uid").toString().equals(currentUserUID)){ //user already signed in
                                       attendeeFound = true;
                                       saveUID = signupList.get(i).get("uid").toString();
                                       checkInString = signupList.get(i).get("checked_in").toString();
                                       int checkInInt = Integer.parseInt(checkInString);
                                       signupList.get(i).replace("checked_in",String.valueOf(checkInInt+1)); //increases check in by 1 for each time user checks in
                                       if (signupList.get(i).get("date_time") != null){ //if date_time key already exists in the specific user's map of signup list
                                           signupList.get(i).replace("date_time", date); //update its value
                                       }else{
                                           signupList.get(i).put("date_time", date); //otherwise add key value pair date_time: value
                                       }
                                       if(signupList.get(i).get("attendee_latitude") != null){
                                           signupList.get(i).replace("attendee_latitude", attendeeLatitude);
                                       }else{
                                           signupList.get(i).put("attendee_latitude", attendeeLatitude);
                                       }
                                       if(signupList.get(i).get("attendee_longitude") != null){
                                           signupList.get(i).replace("attendee_longitude", attendeeLongitude);
                                       }else{
                                           signupList.get(i).put("attendee_longitude", attendeeLongitude);
                                       }
                                   }
                               }
                               if (attendeeFound){// user already signed in and is checking in
                                   attendeeFound = false;
                                   eventsDocRef.update("signed_up", signupList)
                                           .addOnCompleteListener(aVoid -> {
                                               // Update UI to reflect that the user has signed up
                                               Toast.makeText(ViewEventActivity.this, "Added check in event successfully!", Toast.LENGTH_SHORT).show();

                                           }); //replaces the old sign up list with the new one
                               }else{ //user has not signed up yet but is scanning the check in barcode

                                   Map<String,Object> userMap = new HashMap<>();
                                   userMap.put("uid", currentUserUID);
                                   userMap.put("checked_in", "1");
                                   userMap.put("date_time", date);
                                   userMap.put("attendee_latitude", attendeeLatitude);
                                   userMap.put("attendee_longitude", attendeeLongitude);
                                   signupList.add(userMap);
                                   eventsDocRef.update("signed_up", signupList)
                                           .addOnCompleteListener(aVoid -> {
                                               // Update UI to reflect that the user has signed up
                                               Toast.makeText(ViewEventActivity.this, "Signed up and checked in successfully!", Toast.LENGTH_SHORT).show();

                                           }); //replaces the old sign up list with the new one

                                   user.update("events", FieldValue.arrayUnion(eventId));
                               }





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
    private Map<String, Object> getLocation(){
        Map<String,Object> latlongMap = new HashMap<>();
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, ViewEventActivity.this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            latlongMap.put("attendee_latitude", location.getLatitude());
            latlongMap.put("attendee_longitude", location.getLongitude());


        }catch(Exception e){
            e.printStackTrace();
        }
        return latlongMap;

    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this,""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_LONG).show();
        Geocoder geocoder = new Geocoder(ViewEventActivity.this, Locale.getDefault());

        geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 5, new Geocoder.GeocodeListener() {
            @Override
            public void onGeocode(List<Address> addresses) {
                for (int i = 0; i < addresses.size(); i++){
                    Log.d("address", addresses.get(i).toString());
                }
                String address = addresses.get(0).getAddressLine(0);
//                addressText.setText(address);

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


