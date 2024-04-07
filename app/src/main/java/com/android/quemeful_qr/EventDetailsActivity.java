package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.osmdroid.views.MapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class activity that handles the event details view for specific user type.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private TextView textViewEventTitle, textViewEventDate, textViewEventTime, textViewEventLocation, textViewEventDescription, current_milestone_text, congratulatoryText;
    private FirebaseFirestore db;
    private ImageView imageViewBackArrow, imageViewEventImage;
    private TextView viewAttendee, textViewScanQR, textViewSignUp;
    private Button buttonSignUp, buttonPromotion;
    private ExtendedFloatingActionButton buttonCheckIn;

    private int[] MILESTONES = {1, 10, 100, 200, 500};
    private String eventId;

    private CardView milestoneCardView;

    //location variables
    private MapView map;
    private Button displayMapPinsActivityButton;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    private Double latitude;
    private Double longitude;
    private TextView addressText;
    Double attLatitude;
    Double attLongitude;
    String attId;


    /**
     * This onCreate method is used to set up an interface with all event details.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetailsactivity);

        //firebase setup
        db = FirebaseFirestore.getInstance();

        imageViewBackArrow = findViewById(R.id.backArrow);
        textViewEventLocation = findViewById(R.id.textViewEventLocation);
        textViewEventTitle = findViewById(R.id.textViewEventTitle);
        textViewEventDate = findViewById(R.id.textViewEventDate);
        textViewEventTime = findViewById(R.id.textViewEventTime);
        textViewEventLocation = findViewById(R.id.textViewEventLocation);
        textViewEventDescription = findViewById(R.id.textViewEventDescription);
        viewAttendee = findViewById(R.id.viewAttendee);

        milestoneCardView = findViewById(R.id.milestone_cardView);
        current_milestone_text = findViewById(R.id.current_milestone_text);
        congratulatoryText = findViewById(R.id.congratulatory_message);
        imageViewEventImage = findViewById(R.id.imageViewEvent);

        textViewScanQR = findViewById(R.id.scanQRTitle);
        textViewSignUp = findViewById(R.id.signUpTitle);
        buttonCheckIn = findViewById(R.id.scanQRButton);
        buttonSignUp = findViewById(R.id.signUpButton);
        buttonPromotion = findViewById(R.id.promotionButton);
        addressText = findViewById(R.id.address_text);

        //map xml
        map = findViewById(R.id.map);
        displayMapPinsActivityButton = findViewById(R.id.display_map_pins_activity_button);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
        loadMap();
        onPause();


        displayMapPinsActivityButton.setOnClickListener(v -> db.collection("events").get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) { //for every document found (loop runs once - only 1 document matches uuid)
                            //brings the user to a new activity with event details

                            String eventName = document.getData().get("title").toString();
                            String eventLocation = document.getData().get("location").toString();
                            double eventLatitude = (Double) document.getData().get("latitude");
                            double eventLongitude = (Double) document.getData().get("longitude");

                            MapPin eventPin = new MapPin(eventName, eventLocation, eventLatitude, eventLongitude);
                            List<MapPin> eventPinList = new ArrayList<MapPin>();
                            eventPinList.add(eventPin);

                            Toast.makeText(EventDetailsActivity.this,
                                    "added: " +
                                            eventLocation + "\nLatitude: " +
                                            eventLatitude + "\nLongitude: " +
                                            eventLongitude, Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < eventPinList.size(); i++){
                                Log.d("value is", eventPinList.get(i).getLocation().toString());
                                GeoPoint eventPinGeoPoint = new GeoPoint(eventPinList.get(i).getLatitude(), eventPinList.get(i).getLongitude());
                                displayMarker(eventPinGeoPoint, eventPin);
                            }

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        //set the error message onto the camera textview "QR code not recognized"
                        Toast.makeText(EventDetailsActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })); // closing displayMapPinsActivityButton onClickListener

        //add pins when tap on map
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                addEventMarker(p);
                latitude = p.getLatitude();
                longitude = p.getLongitude();
                Toast.makeText(EventDetailsActivity.this,
                        "Lat: " + p.getLatitude() + ", Long: " + p.getLongitude(), Toast.LENGTH_LONG).show();
                new EventDetailsActivity.fetchData().start();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                // write your code here
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

                displayEventPins();
                displayAttendeePins();

            }
        }); // closing displayMapPinsActivityButton onClickListener
//        //add pins when tap on map
//        MapEventsReceiver mReceive = new MapEventsReceiver() {
//
//            @Override
//            public boolean singleTapConfirmedHelper(GeoPoint p) {
//
//                addEventMarker(p);
//                latitude = p.getLatitude();
//                longitude = p.getLongitude();
//                Toast.makeText(EventDetailsActivity.this,
//                        "Lat: " + p.getLatitude() + ", Long: " + p.getLongitude(), Toast.LENGTH_LONG).show();
//                new EventDetailsActivity.fetchData().start();
//                return true;
//            }
//
//            @Override
//            public boolean longPressHelper(GeoPoint p) {
//                // write your code here
//                return false;
//            }
//        };
//        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
//        map.getOverlays().add(OverlayEvents);

        // navigate back to previous page on clicking the back arrow.
        imageViewBackArrow.setOnClickListener(v -> finish());

    

        eventId = getIntent().getStringExtra("event_id");

        if (eventId != null) {
            fetchEventDetails(eventId);
            setupSignUpButton(eventId);
            setupCheckInButton();
            setupPromotionButton();

            // on click on viewAttendee it navigates to the list of attendees.
            viewAttendee.setOnClickListener(v -> navigateToListOfAttendees(eventId));

            milestoneCardView.setOnClickListener(v -> navigateToLMilestone(eventId));

        } else {
            // Handle the error
        }
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) document.get("signed_up");
                    if (signedUpUsers.size() > 0) {

                        congratulatoryText.setVisibility(View.VISIBLE);
                    }

                    int nextMilestone = 0;
                    for (int milestone : MILESTONES) {
                        if (signedUpUsers.size() < milestone) {
                            nextMilestone = milestone;
                            break;
                        }
                    }
                    current_milestone_text.setText( "Next Milestone: " + signedUpUsers.size() + "/" + nextMilestone);
                }}});

    }

    public void displayEventPins(){
        db.collection("events").whereEqualTo("id", eventId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) { //for every document found (loop runs once - only 1 document matches uuid)
                                //brings the user to a new activity with event details

                                String eventName = document.getData().get("title").toString();
                                String eventLocation = document.getData().get("location").toString();
                                double eventLatitude = (Double) document.getData().get("latitude");
                                double eventLongitude = (Double) document.getData().get("longitude");

                                EventMapPin eventPin = new EventMapPin(eventName, eventLocation, eventLatitude, eventLongitude);
                                List<EventMapPin> eventPinList = new ArrayList<EventMapPin>();
                                eventPinList.add(eventPin);

                                Toast.makeText(EventDetailsActivity.this,
                                        "added: " +
                                                eventLocation + "\nLatitude: " +
                                                eventLatitude + "\nLongitude: " +
                                                eventLongitude, Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < eventPinList.size(); i++){
                                    Log.d("value is", eventPinList.get(i).getLocation().toString());
                                    GeoPoint eventPinGeoPoint = new GeoPoint(eventPinList.get(i).getLatitude(), eventPinList.get(i).getLongitude());
                                    displayEventMarker(eventPinGeoPoint, eventPin);
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            //set the error message onto the camera textview "QR code not recognized"
                            Toast.makeText(EventDetailsActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void displayAttendeePins(){



        db.collection("events").whereEqualTo("id", eventId).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //for every document found (loop runs once - only 1 document matches uuid)
                    Log.d("check query", document.getData().get("title").toString());
                    //brings the user to a new activity with event details
                    List<Map<String, Object>> signupList = (List<Map<String, Object>>) document.get("signed_up");
                    List<AttendeeMapPin> attendeePinList = new ArrayList<AttendeeMapPin>();

                    for (int i = 0; i < signupList.size(); i++) {
                            attId = (String) signupList.get(i).get("uid");
                            Log.d("attId", attId);
                            if ((Double) signupList.get(i).get("attendee_latitude") != null && (Double) signupList.get(i).get("attendee_latitude") != null){
                                attLatitude = (Double) signupList.get(i).get("attendee_latitude");
                                attLongitude = (Double) signupList.get(i).get("attendee_longitude");
                                AttendeeMapPin attendeePin = new AttendeeMapPin(attId, attLatitude,attLongitude);
                                attendeePinList.add(attendeePin);
                                Toast.makeText(EventDetailsActivity.this,
                                        "added: " +
                                                attLatitude + "\nLatitude: " +
                                                attLongitude + "\nLongitude: " +
                                                attId, Toast.LENGTH_SHORT).show();
                            }

                    }
                    if (!attendeePinList.isEmpty()){
                        for (int j = 0; j < attendeePinList.size(); j++) {
                            GeoPoint attendeePinGeoPoint = new GeoPoint(attendeePinList.get(j).getLatitude(), attendeePinList.get(j).getLongitude());
                            displayAttendeeMarker(attendeePinGeoPoint, attendeePinList.get(j));
                        }
                    }else{
                        Toast.makeText(EventDetailsActivity.this,
                                "no attendees", Toast.LENGTH_SHORT).show();
                    }




                }
            }
        });


    }
    public void attendeeCollectionQuery(){
//        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.d("myid", currentUserUID);
//        db.collection("users").whereEqualTo("uid", currentUserUID).get().addOnCompleteListener(task -> {
//
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    //for every document found (loop runs once - only 1 document matches uuid)
//                    Log.d("first name", document.getData().get("firstName").toString());
//                    //brings the user to a new activity with event details
//                }
//            }
//        });
    }
//





    public void addAttendeeMarker (GeoPoint center){
        //loop through array of overlays until find the correct overlay id to remove
        for(int i=0 ;i<map.getOverlays().size();i++){
            Overlay overlay=map.getOverlays().get(i);
            if(overlay instanceof Marker &&((Marker) overlay).getId().equals("Attendee pin overlay")){
                map.getOverlays().remove(overlay);
            }
        }
        Marker marker = new Marker(map);
        marker.setId("Attendee pin overlay");
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getDrawable(R.drawable.attendee_pin_icon));
        map.getOverlays().add(marker);
        marker.setTitle("Attendee point");
        map.invalidate();
    }

    public void addEventMarker (GeoPoint center){
        //loop through array of overlays until find the correct overlay id to remove
        for(int i=0 ;i<map.getOverlays().size();i++){
            Overlay overlay=map.getOverlays().get(i);
            if(overlay instanceof Marker&&((Marker) overlay).getId().equals("Event pin overlay")){
                map.getOverlays().remove(overlay);
            }
        }
        //if that overlay id doesn't exist then add a new marker
        Marker marker = new Marker(map);
        marker.setId("Event pin overlay");
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getDrawable(R.drawable.event_pin_icon));
        map.getOverlays().add(marker);
        marker.setTitle("Event point");
        map.invalidate();
    }
    public void displayAttendeeMarker(GeoPoint center, AttendeeMapPin pin){
        Marker marker = new Marker(map);
        marker.setId("Display events overlay");
        marker.setPosition(center);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getDrawable(R.drawable.attendee_pin_icon));

        map.getOverlays().add(marker);
        marker.setTitle(pin.getAttendeeId());
        map.invalidate();
    }
    public void displayEventMarker (GeoPoint center, EventMapPin pin){

    public void displayMarker (GeoPoint center, MapPin pin){
        Marker marker = new Marker(map);
        marker.setId("Display events overlay");
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getDrawable(R.drawable.event_pin_icon));
        map.getOverlays().add(marker);
        marker.setTitle(pin.getTitle() + "\n" + pin.getLocation());
        map.invalidate();
    }

    protected void loadMap(){
        // Request runtime Location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Location Permission GRANTED");
        } else {
            System.out.println("Location Permission DENIED");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        }
        // Create MapView
        map = findViewById(R.id.map);
        // Set tile source + display settings
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        // Create MapController and set starting location
        mapController = map.getController();
        // Create location overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mapController.setZoom(15.5);
        map.getOverlays().add(myLocationOverlay);
    }

    //map methods: onResume() and onPause()
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    class fetchData extends Thread{
        private String data = "";
        @Override
        public void run(){
            try{
                URL url = new URL("https://nominatim.openstreetmap.org/reverse?lat=" + latitude +
                        "&lon=" + longitude + "&format=json");
                Log.d("URL latitude", String.valueOf(latitude));
                Log.d("URL longitude", String.valueOf(longitude));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }
                if(!data.isEmpty()){
                    JSONObject jsonObject = new JSONObject(data);
                    String addressName = jsonObject.getString("display_name");
                    Log.d("address name", addressName);
                    runOnUiThread(() -> {
                        // Stuff that updates the UI
                    });
                }

            } catch (JSONException | IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    /**
     * This method is used to set the button to sign up for an event.
     * @param eventId the event being signed up for (identified with its specific id).
     */
    private void setupSignUpButton(String eventId) {
        buttonSignUp.setOnClickListener(view -> signUpForEvent(eventId));
    }

    /**
     * This method is used to set the button to check-in to an event using the QR code.
     */
    private void setupCheckInButton(){
        buttonCheckIn.setOnClickListener(v -> openScanQRActivity());
    }

    /**
     * This method is used to set the button to add promotions (by organizer) for an event.
     */
    private void setupPromotionButton() {
        buttonPromotion.setOnClickListener(v -> openEventPromotionActivity());
    }

    /**
     * This method is used to sign up for an event.
     * @param eventId the event being signed up for (identified with its specific id).
     */
    private void signUpForEvent(String eventId) {
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference eventRef = db.collection("events").document(eventId);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", currentUserUID);
        userMap.put("checked_in", "0"); // Assuming "0" means not checked-in and "1" means checked-in
        FirebaseMessaging.getInstance().subscribeToTopic(eventId);
        DocumentReference user = db.collection("users").document(currentUserUID);
        user.update("events", FieldValue.arrayUnion(eventId));
        eventRef.update("signed_up", FieldValue.arrayUnion(userMap))
                .addOnSuccessListener(aVoid -> {
                    // Update UI to reflect that the user has signed up
                    Toast.makeText(EventDetailsActivity.this, "Signed up for event successfully!", Toast.LENGTH_SHORT).show();
                    updateUIBasedOnUserStatus(true, false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                        }
                    }
                    eventRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String title = document.getString("title");
                                List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) document.get("signed_up");
                                String description;
                                for (int milestone : MILESTONES) {
                                    if (document.get("signed_up") != null) {

                                        if (signedUpUsers.size() == milestone) {
                                            if (milestone == 1){
                                                description = "You just got your first attendee!";
                                            } else {
                                                description = "You have reached " + milestone + " attendees!";
                                            }
                                            String token = document.getString("organizer_token");
                                            SendNotifications sendNotifications = new SendNotifications();
                                            sendNotifications.sendNotification(title, "You just hit a Milestone!\uD83C\uDF89. " + description, token, "party");
                                        }}}}}});

                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    /**
     * This method is used to navigate to the list of attendees attending a specific event.
     * @param eventId the event being attended (identified with its specific id).
     */
    private void navigateToListOfAttendees(String eventId) {
        AttendeesList attendeesFragment = new AttendeesList(eventId);

        // Begin a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, attendeesFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void navigateToLMilestone(String eventId) {
        Milestone milestoneFragment = new Milestone(eventId);

        // Begin a transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, milestoneFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * This method is used to fetch the event details with its specific id from the firebase,
     * and display them to the user.
     * @param eventId the event being attended/signed up/checked in (identified with its specific id).
     */
    private void fetchEventDetails (String eventId) {

        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                EventHelper event = documentSnapshot.toObject(EventHelper.class);
                if (event != null) {
                    textViewEventTitle.setText(event.getTitle());
                    textViewEventDate.setText(event.getDate());
                    textViewEventTime.setText(event.getTime());
                    textViewEventLocation.setText(event.getLocation());
                    textViewEventDescription.setText(event.getDescription());

                    // Update UI for organizer or general user
                    if (currentUserUID.equals(event.getOrganizer())) {
                        updateUIForOrganizer();
                    } else {
                        updateUIForGeneralUser(currentUserUID, event, documentSnapshot);
                    }

                    // Decode and set the image
                    if (event.getPoster() != null && !event.getPoster().trim().isEmpty()) {
                        byte[] decodedString = Base64.decode(event.getPoster().trim(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageViewEventImage.setImageBitmap(decodedByte);
                    } else {
                        imageViewEventImage.setImageResource(R.drawable.ic_launcher_background); // Default or placeholder image.
                    }
                }
            } else {}
        }).addOnFailureListener(e -> {
        });
    }

    /**
     * This method is used to change the UI when an organizer views event details.
     */
    private void updateUIForOrganizer() {
        textViewScanQR.setVisibility(View.GONE);
        buttonCheckIn.setVisibility(View.GONE);
        textViewSignUp.setVisibility(View.GONE);
        buttonSignUp.setVisibility(View.GONE);
        viewAttendee.setVisibility(View.VISIBLE);
        buttonPromotion.setVisibility(View.VISIBLE);
        map.setVisibility(View.VISIBLE);
        displayMapPinsActivityButton.setVisibility(View.VISIBLE);
        addressText.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used to update the UI for a non-attendee.
     * @param currentUserUID the id to identify the current user (attendee/organiser/admin).
     * @param event the event in concern.
     * @param documentSnapshot the list that records events,
     *                        used to fetch status of whether the user is signed up to the event or not.
     */
    private void updateUIForGeneralUser(String currentUserUID, EventHelper event, DocumentSnapshot documentSnapshot) {
        // Your existing logic to check if the user is signed up or checked in...
        List<Map<String, Object>> signedUpUsers = (List<Map<String, Object>>) documentSnapshot.get("signed_up");
        boolean isUserSignedUp = false;
        boolean isUserCheckedIn = false;

        if (signedUpUsers != null) {
            for (Map<String, Object> userMap : signedUpUsers) {
                String uid = (String) userMap.get("uid");
                String checkedIn = (String) userMap.get("checked_in");
                if (currentUserUID.equals(uid)) {
                    isUserSignedUp = true;
                    isUserCheckedIn = "1".equals(checkedIn);
                    break;
                }
            }
        }
        updateUIBasedOnUserStatus(isUserSignedUp, isUserCheckedIn);
    }

    /**
     * This method is used to change the visibility when a user is signed up.
     * @param isUserSignedUp checks the status whether the user is signed-up or not.
     * @param isUserCheckedIn checks the status whether the user is checked-in or not.
     */
    private void updateUIBasedOnUserStatus(boolean isUserSignedUp, boolean isUserCheckedIn) {
        TextView textViewScanQR = findViewById(R.id.scanQRTitle);
        Button buttonCheckIn = findViewById(R.id.scanQRButton);
        TextView textViewSignUp = findViewById(R.id.signUpTitle);
        Button buttonSignUp = findViewById(R.id.signUpButton);
        CardView milestone = findViewById(R.id.milestone_cardView);

        if (isUserSignedUp) {
            textViewSignUp.setVisibility(View.GONE);
            buttonSignUp.setVisibility(View.GONE);
            viewAttendee.setVisibility(View.GONE);
            buttonPromotion.setVisibility(View.GONE); // show promotion button for signed up users
            milestone.setVisibility(View.GONE);

            if (isUserCheckedIn) {
                textViewScanQR.setVisibility(View.GONE);
                buttonCheckIn.setVisibility(View.GONE);
                // Possibly show a message saying "You are checked in"
                Toast.makeText(getBaseContext(), "You are already Checked-In", Toast.LENGTH_LONG).show();
                buttonPromotion.setVisibility(View.VISIBLE); // show promotion button for checked in users
                buttonCheckIn.setVisibility(View.VISIBLE);
            } else {
                textViewScanQR.setVisibility(View.VISIBLE);
                buttonCheckIn.setVisibility(View.VISIBLE);
            }
        } else {
            textViewScanQR.setVisibility(View.GONE);
            buttonCheckIn.setVisibility(View.GONE);
            viewAttendee.setVisibility(View.GONE);
            textViewSignUp.setVisibility(View.VISIBLE);
            buttonSignUp.setVisibility(View.VISIBLE);
            buttonPromotion.setVisibility(View.GONE); // show promotion button for not signed up users
            milestone.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to start the EventPromotionActivity when promotion button is clicked.
     * (only accessible when the user-type: organizer)
     */
    private void openEventPromotionActivity() {
        Intent intent = new Intent(EventDetailsActivity.this, EventPromotionActivity.class);
        String eventId = getIntent().getStringExtra("event_id");
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    /**
     * This method is used when the user clicks on the check-in button to scan the QR code.
     * It works by starting another activity that handles QR code scanning.
     */
    protected void openScanQRActivity(){
        Intent intent = new Intent(EventDetailsActivity.this, ScanQRActivity.class);
        startActivity(intent);
    }

    /**
     * This method is used to handle permission from user on receiving notifications about the specific event.
     * @param requestCode The request code passed.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications not allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }
} // class closing