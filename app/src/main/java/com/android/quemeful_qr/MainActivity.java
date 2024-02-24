package com.android.quemeful_qr;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
//                    setContentView(R.layout.activity_main);
                    setContentView(R.layout.fragment_home);















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
                Log.e(TAG, "Error checking user document", task.getException());
                setContentView(R.layout.nologin);
            }
        });
    }
}
