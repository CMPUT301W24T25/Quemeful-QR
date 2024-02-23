package com.android.quemeful_qr;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.dynamic.OnDelegateCreatedListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MeowBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_dashboard_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_event_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_account_circle_24));

        bottomNavigation.setOnClickMenuListener(item -> {
            // Debug code: REMOVE later
            // your codes
            Toast.makeText(MainActivity.this,
                    "clicked item :" + item.getId(),
                    Toast.LENGTH_SHORT).show();
        });

        String Eventid  = "sgY7grFJTVzKhTEXAX9A";


        bottomNavigation.setOnShowListener(item -> {
            Fragment fragment = null;
            switch (item.getId()){
                case 1:
                    fragment = new Home();
                    break;
                case 2:

                    fragment = new list_of_attendees(Eventid);
                    //fragment = new Events();
                    break;
                case 3:
                    fragment = new Profile();
                    break;
            }
            loadFragment(fragment);
        });

        bottomNavigation.show(1, true);
        askNotificationPermission();
        new sign_up_to_notificaition(Eventid);
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notification Permission Approved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Give Notification permission to receive Event Announcements", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Give Notification permission to receive Event Announcements", Toast.LENGTH_SHORT).show();
                }
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WAKE_LOCK) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WAKE_LOCK);
            }
        }
    }
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}