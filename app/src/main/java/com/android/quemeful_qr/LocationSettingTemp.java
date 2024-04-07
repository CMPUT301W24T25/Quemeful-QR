package com.android.quemeful_qr;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class LocationSettingTemp {

//    the switch is called geolocationSwitch
//    add android:name=".UserSettings" to android manifest
//
//    private SwitchMaterial locationSwitch;
//    private UserSettings settings;
//    private TextView titleTV;
//
//
//    //sharedPreferences onCreate
//    settings = (UserSettings) getApplication();
//    initWidgets();
//    loadSharedPreferences();
//    initSwitchListener();
//
//    //sharedPreferences
//    private void initSwitchListener(){
//        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
//                if(checked)
//                    settings.setCustomTheme(UserSettings.DARK_THEME);
//                else
//                    settings.setCustomTheme(UserSettings.LIGHT_THEME);
//                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
//                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
//                editor.apply();
//                updateView();
//
//            }
//        });
//        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean locationSwitchChecked) {
//                if (locationSwitchChecked){
//                    settings.setCustomLocation(UserSettings.LOCATION_ON);
//                }else{
//                    settings.setCustomLocation(UserSettings.LOCATION_OFF);
//                }
//                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.LOCATION_PREFERENCES, MODE_PRIVATE).edit();
//                editor.putString(UserSettings.CUSTOM_LOCATION, settings.getCustomLocation());
//                editor.apply();
//                updateView();
//            }
//        });
//    }
//    //sharedPreferences
//    private void updateView(){ //saves the settings after closing the app
//        final int blue = Color.parseColor("#2196F3");
//        final int white = ContextCompat.getColor(this, R.color.white);
//        if (settings.getCustomTheme().equals(UserSettings.DARK_THEME)){
//            titleTV.setTextColor(blue);
//            themeSwitch.setChecked(true);
//
//        }else{
//            titleTV.setTextColor(white);
//            themeSwitch.setChecked(false);
//        }
//        if(settings.getCustomLocation().equals(UserSettings.LOCATION_OFF)){
////            openAppSettings();
//            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                // permission granted
//                Toast.makeText(getApplicationContext(),"Permission granted.",Toast.LENGTH_SHORT).show();
//            }else {
//                // permission not granted
//                Toast.makeText(getApplicationContext(),"Permission not granted.",Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        1
//                );
//            }
//        }else{
//
//        }
//
//
//
//    }
//
//    protected void openAppSettings(){
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivity(intent);
//    }
//    //sharedPreferences
//    private void loadSharedPreferences(){
//        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
//        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.LIGHT_THEME);
//        settings.setCustomTheme(theme);
//        Log.d("theme settings", theme);
//        //location settings
//        SharedPreferences sharedPreferencesLocation = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
//        String locationPermissions = sharedPreferencesLocation.getString(UserSettings.LOCATION_PREFERENCES, UserSettings.LOCATION_OFF);
//        settings.setCustomLocation(locationPermissions);
//        Log.d("location settings", locationPermissions);
//        updateView();
//    }
//    //sharedPreferences
//    private void initWidgets() {
//        themeSwitch = findViewById(R.id.theme_switch);
//        titleTV = findViewById(R.id.title_tv);
//        locationSwitch = findViewById(R.id.location_switch);
//    }


}

