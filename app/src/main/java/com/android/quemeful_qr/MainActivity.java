package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String userFirstName = "";
    private String userLastName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nologin);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to retrieve the user document by the device ID
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
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
                Log.e(TAG, "Error checking user document", task.getException());
                setContentView(R.layout.nologin);
            }
        });
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
                    fragment = Profile.newInstance(userFirstName, userLastName); // Consider passing user data to the Profile fragment here
                    break;
            }
            loadFragment(fragment);
        });

        // Setting a no-op ReselectListener to avoid NullPointerException
        bottomNavigation.setOnReselectListener(item -> {
            // Implement reselection logic here if needed, otherwise leave as no-op to prevent NullPointerException
        });

        bottomNavigation.show(1, true);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}