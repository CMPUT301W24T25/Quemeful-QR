package com.android.quemeful_qr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Tag for logging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the device's unique ID
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the Firestore database instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Attempt to retrieve the user document by the device ID
        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Document exists, meaning the user is "logged in"
                    Log.d(TAG, "User exists with ID: " + deviceId);

                    // still set content view to no login (instead of activity main) page but do not type in any inputs just login automatically
                    setContentView(R.layout.nologin);

//                    // autofill existing data
//                    EditText firstNameEditText = findViewById(R.id.firstNameEditText);
//                    EditText lastNameEditText = findViewById(R.id.lastNameEditText);
                    Button skipLogin = findViewById(R.id.SkipLoginForExistingUsersButton);

//                    // Get the entered names that is saved
//                    SharedPreferences preferences = getSharedPreferences("LoginData", MODE_PRIVATE);
//                    String firstName = preferences.getString("First Name", "");
//                    String lastName = preferences.getString("Last Name", "");
//
//                    //If field is empty set text
//                    if(!firstName.isEmpty() && !lastName.isEmpty()){
//
//                        //autofill
//                        firstNameEditText.setText(firstName);
//                        lastNameEditText.setText(lastName);
//                        // optionally displaying a toast
//                        Toast.makeText(MainActivity.this, "Username Already Exists, Logging In", Toast.LENGTH_SHORT).show();
//                        // after displaying message simply click on already exists button and proceed to next activity
//                        skipLogin.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                // already checked In user proceeds to the next activity - jahnabi
//                                Intent intent = new Intent(MainActivity.this, QRCheckActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                    }
                    // optionally displaying a toast
                    Toast.makeText(MainActivity.this, "Username Already Exists, Logging In", Toast.LENGTH_SHORT).show();
                    // after displaying message simply click on already exists button and proceed to next activity
                    skipLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // already checked In user proceeds to the next activity - jahnabi
                            Intent intent = new Intent(MainActivity.this, QRCheckActivity.class);
                            startActivity(intent);
                        }
                    });

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
                                        // Optionally, just for checking purpose switching to the next QRCheck activity - jahnabi
                                        //setContentView(R.layout.activity_main);
                                        Intent intent = new Intent(MainActivity.this, QRCheckActivity.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error adding new user", e));
                        }
                    });
                }
            } else {
                // Task failed, handle the error
                Log.e(TAG, "Error checking user document", task.getException());
                setContentView(R.layout.nologin);
            }
        });
    }
}
