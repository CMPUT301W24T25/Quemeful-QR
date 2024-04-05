//https://www.youtube.com/watch?v=Rf7FLv7iAqU
//https://stackoverflow.com/a/40142454
//https://stackoverflow.com/a/32983128
//https://stackoverflow.com/a/6467904
//https://www.geoglify.com/blog/openstreetmap-nominatim-api-mastery/
//https://www.youtube.com/watch?v=5lNQLR53UtY
//https://stackoverflow.com/a/5162096
/**
 * This is an activity class used to enable location/map functions, where the event takes place for the users.
 * Reference URLs:
 * https://github.com/osmdroid/osmdroid/issues/1304#issuecomment-477920497
 * Author- (osmdroid) neoacevedo, License- Apache 2.0, Published Date- 28 Mar, 2019
 * https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
 * Author- osmdroid, License- Apache 2.0, Published Date- 19 May, 2022
 * https://stackoverflow.com/questions/34139048/cannot-resolve-manifest-permission-access-fine-location
 * Author- anoop ghildiyal, License- CC BY-SA 3.0, Published Date- 17 Mar, 2017
 * https://stackoverflow.com/a/63456832
 * Author- rilott, License- CC BY-SA 4.0, Published Date- 17 Aug, 2020
 * https://stackoverflow.com/a/71698834
 * Author- REInVent, License- CC BY-SA 4.0, Published Date- 31 Mar, 2022
 */
package com.android.quemeful_qr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
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
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    private MapView map;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;

    private Button returnToCurrentLocation;
    private Button searchMapButton;
    private Button confirmLocationButton;

    private Button locationSettingsButton;
    private Button clearAddressButton;
    private EditText addressText;


    //    private double latitude = 41.1616;
//    private double longitude = -8.5856;
    private double latitude;
    private double longitude;

    private LocationManager locationManager;
    private int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load and set layout
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);

        returnToCurrentLocation = (Button) findViewById(R.id.return_to_current_location);
        searchMapButton = (Button) findViewById(R.id.search_map_button);
        confirmLocationButton = (Button) findViewById(R.id.confirm_location_button);
        locationSettingsButton = (Button) findViewById(R.id.location_settings_button);
        clearAddressButton = (Button) findViewById(R.id.clear_address_button);
        addressText = findViewById(R.id.address_text);

        loadMap();
        onPause();

        searchMapButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                locationName = addressText.getText().toString();

                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    List<Address> geoResults = geocoder.getFromLocationName(locationName, 1);

                    if (geoResults != null && !geoResults.isEmpty()) {
                        Address addr = geoResults.get(0);
                        latitude = addr.getLatitude();
                        longitude = addr.getLongitude();
                        GeoPoint location = new GeoPoint(addr.getLatitude(), addr.getLongitude());


                        mapController.animateTo(location);
                        mapController.setZoom(15.5);
                        mapController.setCenter(location);
                        onPause();
                        addEventMarker(location);


                        Toast.makeText(getApplicationContext(), "Location: " + locationName + "\n" + latitude + ", " + longitude, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.d("catch", e.toString());
                    Toast.makeText(getApplicationContext(), "Failed Search", Toast.LENGTH_LONG).show();
                }


            }
        });
        returnToCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if location permissions are granted
                mapController.animateTo(myLocationOverlay.getMyLocation());
                mapController.setZoom(15.5);
                mapController.setCenter(myLocationOverlay.getMyLocation());
                onPause();
                addAttendeeMarker(myLocationOverlay.getMyLocation());
            }
        });

        locationSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppSettings();
            }
        });

        clearAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressText.setText("");
            }
        });

        confirmLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("location string",addressText.getText().toString());
                returnIntent.putExtra("location latitude", latitude);
                returnIntent.putExtra("location longitude", longitude);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });

        //add pins when tap on map
        MapEventsReceiver mReceive = new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                addEventMarker(p);
                latitude = p.getLatitude();
                longitude = p.getLongitude();
                Toast.makeText(MapActivity.this,
                        "Lat: " + p.getLatitude() + ", Long: " + p.getLongitude(), Toast.LENGTH_LONG).show();
                new fetchData().start();
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
    }

    protected void openAppSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    protected void loadMap(){
        // Request Location permission
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
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mapController.animateTo(myLocationOverlay.getMyLocation());
                        mapController.setZoom(15.5);
                        mapController.setCenter(myLocationOverlay.getMyLocation());

                    }
                });
            }
        });
        map.getOverlays().add(myLocationOverlay);

        // Set user agent
        Configuration.getInstance().setUserAgentValue("RossMaps");

        System.out.println(myLocationOverlay.getMyLocation());
        System.out.println("Create done");

    }


    public void addAttendeeMarker (GeoPoint center){
        //loop through array of overlays until find the correct overlay id to remove
        for(int i=0 ;i<map.getOverlays().size();i++){
            Overlay overlay=map.getOverlays().get(i);
            if(overlay instanceof Marker&&((Marker) overlay).getId().equals("Attendee pin overlay")){
                map.getOverlays().remove(overlay);
            }
        }
        Marker marker = new Marker(map);
        marker.setId("Attendee pin overlay");
        marker.setPosition(center);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getDrawable(R.drawable.attendee_pin_icon));
//        map.getOverlays().clear();
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
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            addressText.setText(addressName);

                        }
                    });



                }


            } catch (JSONException | IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

}

