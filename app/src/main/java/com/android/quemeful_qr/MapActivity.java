//https://github.com/osmdroid/osmdroid/issues/1304#issuecomment-477920497
//https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
//https://stackoverflow.com/a/34139211
//https://stackoverflow.com/a/63456832
//https://stackoverflow.com/a/71698834
//https://stackoverflow.com/a/69148289
package com.android.quemeful_qr;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.sql.DriverManager.println;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private ImageView backButton;
    private Button returnToCurrentLocation;
    private EditText searchMapEditText;
    private Button searchMapButton;

    private MapView map = null;
    private IMapController mapController;

    private MyLocationNewOverlay mLocationOverlay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here





        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_map);

        map = (MapView) findViewById(R.id.map);
        backButton = (ImageView) findViewById(R.id.backArrow);
        returnToCurrentLocation = (Button) findViewById(R.id.return_to_current_location);
        searchMapEditText = (EditText) findViewById(R.id.search_map_edittext);
        searchMapButton = (Button) findViewById(R.id.search_map_button);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        returnToCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.animateTo(mLocationOverlay.getMyLocation());
                mapController.setZoom(15.5);
            }
        });

        searchMapButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final String locationName = searchMapEditText.getText().toString();

                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    List<Address> geoResults = geocoder.getFromLocationName(locationName, 1);

                    if (geoResults != null && !geoResults.isEmpty()) {
                        Address addr = geoResults.get(0);

                        GeoPoint location = new GeoPoint(addr.getLatitude(), addr.getLongitude());
                        mapController.animateTo(location);
                        mapController.setZoom(15.5);
                        Toast.makeText(getApplicationContext(), addr.toString(), Toast.LENGTH_LONG).show();

                    } else {
                    Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_LONG).show();
                }
                } catch (Exception e) {
                    Log.d("catch", e.getStackTrace().toString());
                }


            }
        });


        String[] strArray = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        // Request Location permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            println("Location Permission GRANTED");
        } else {
            println("Location Permission DENIED");
            ActivityCompat.requestPermissions(
                    this,
                    strArray,
                    1
            );
        }


        // Create MapView
        map = findViewById(R.id.map);
        // Set tile source + display settings
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

        // Create MapController and set starting location
        mapController = map.getController();

        GpsMyLocationProvider prov = new GpsMyLocationProvider(ctx);
        prov.addLocationSource(LocationManager.NETWORK_PROVIDER);
        // Create location overlay
        this.mLocationOverlay = new MyLocationNewOverlay(prov,map);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();
        this.mLocationOverlay.setDrawAccuracyEnabled(true);

        map.getOverlays().add(this.mLocationOverlay);
        //gets the current location
        mLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapController.animateTo(mLocationOverlay.getMyLocation());
                        mapController.setZoom(15.5);
                    }
                });
            }
        });



        // Set user agent
        Configuration.getInstance().setUserAgentValue("RossMaps");

        println(String.valueOf(mLocationOverlay.getMyLocation()));
        println("Create done");


    }


    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);

        }
    }


    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }



    }
}