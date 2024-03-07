package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
<<<<<<< HEAD
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
=======
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.messaging.FirebaseMessaging;
>>>>>>> ebe18dae8eade760676aa43dc4e74714d486f381

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

<<<<<<< HEAD
    // Tag for logging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the device's unique ID
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the Firestore database instance
=======
    private String userFirstName = "";
    private String userLastName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nologin);
        FirebaseApp.initializeApp(this);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
>>>>>>> ebe18dae8eade760676aa43dc4e74714d486f381
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to retrieve the user document by the device ID
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
<<<<<<< HEAD
                    // Document exists, meaning the user is "logged in"
                    Log.d(TAG, "User exists with ID: " + deviceId);
                    setContentView(R.layout.activity_main);
                } else {
                    // No such document exists, meaning the user is not "logged in"
                    Log.d(TAG, "No user found with ID: " + deviceId);
                    // Show the nologin layout for new users
                    setContentView(R.layout.nologin);

                    // Initialize EditTexts and Button
                    EditText firstNameEditText = findViewById(R.id.firstNameEditText);
                    EditText lastNameEditText = findViewById(R.id.lastNameEditText);
                    Button submitDetailsButton = findViewById(R.id.getStartedButton);

                    // Set an OnClickListener on the Button to get user input and update Firestore
                    submitDetailsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Get the entered names
                            String firstName = firstNameEditText.getText().toString();
                            String lastName = lastNameEditText.getText().toString();

                            // Create a new user object
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("uid", deviceId);
                            newUser.put("firstName", firstName);
                            newUser.put("lastName", lastName);

                            // Add the new user to Firestore
                            db.collection("users").document(deviceId).set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "New user added with details");
                                        // Optionally, switch to the main activity layout or another activity after successful submission
                                        setContentView(R.layout.activity_main);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error adding new user", e));
                        }
                    });
                }
            } else {
                // Task failed, handle the error
=======
                    // Document exists, user "logged in"
                    Log.d(TAG, "User exists with ID: " + deviceId);

                    userFirstName = task.getResult().getString("firstName");
                    userLastName = task.getResult().getString("lastName");

                    setContentView(R.layout.activity_main);
                    initializeBottomNavigation(); // Initialize bottom navigation here
                } else {
                    // No such document, user not "logged in"
                    Log.d(TAG, "No user found with ID: " + deviceId);
                    setContentView(R.layout.nologin);
                    handleNewUserInput(db, deviceId); // Handle new user input
                }
            } else {
>>>>>>> ebe18dae8eade760676aa43dc4e74714d486f381
                Log.e(TAG, "Error checking user document", task.getException());
                setContentView(R.layout.nologin);
            }
        });
<<<<<<< HEAD
=======
    }

    private void handleNewUserInput(FirebaseFirestore db, String deviceId) {
        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        Button submitDetailsButton = findViewById(R.id.getStartedButton);

        submitDetailsButton.setOnClickListener(view -> {
//            String firstName = firstNameEditText.getText().toString();
//            String lastName = lastNameEditText.getText().toString();
            userFirstName = firstNameEditText.getText().toString();
            userLastName = lastNameEditText.getText().toString();

            Map<String, Object> newUser = new HashMap<>();
            newUser.put("uid", deviceId);
            newUser.put("firstName", userFirstName);
            newUser.put("lastName", userLastName);

            db.collection("users").document(deviceId).set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "New user added with details");
                        setContentView(R.layout.activity_main);
                        initializeBottomNavigation(); // Initialize bottom navigation after user addition
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding new user", e));
        });
    }

    private void initializeBottomNavigation() {

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_dashboard_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_event_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_account_circle_24));

        bottomNavigation.setOnClickMenuListener(item -> {
            // Handle click

        });

        bottomNavigation.setOnShowListener(item -> {
            Fragment fragment = null;
            switch (item.getId()){
                case 1:
                    fragment = new Home();
                    break;
                case 2:
                    fragment = new Events();
                    break;
                case 3:
                    fragment = Profile.newInstance(userFirstName, userLastName);
                    break;
            }
            loadFragment(fragment);
        });

        // Setting a no-op ReselectListener to avoid NullPointerException
        bottomNavigation.setOnReselectListener(item -> {
            // Implement reselection logic here if needed, otherwise leave as no-op to prevent NullPointerException
        });

        bottomNavigation.show(1, true);
        FirebaseMessaging.getInstance().subscribeToTopic("events");
        if ( NotificationManagerCompat.from(this).areNotificationsEnabled()) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
    }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            // Get the current token
                            String token = task.getResult();
                            Log.d(TAG, "Current device token: " + token);

                            // You can use this token as needed in your app
                        } else {
                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                        }
                    }
                });

    }


    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Notifications not allowed", Toast.LENGTH_SHORT).show();
            }
        }
>>>>>>> ebe18dae8eade760676aa43dc4e74714d486f381
    }
}
