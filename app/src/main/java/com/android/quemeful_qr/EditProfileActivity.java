package com.android.quemeful_qr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caverock.androidsvg.SVG;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is a class activity that handles the profile editing functionality for a user.
 */

public class EditProfileActivity extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText homePageEditText;
    private EditText contactEditText;
    private EditText bioEditText;
    private TextView changeAvatarTextView;
    private ImageView deletePic;
    private Button saveButton;
    private ImageView avatarImageView;
    private Uri imageUri;
    private String deviceId;
    private final ActivityResultLauncher<Intent> getContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this)
                            .load(imageUri)
                            .into(avatarImageView); // Display the selected image.
                }
            }
    );

    /**
     * This onCreate method is used to create the view for editing profile names, pictures, buttons.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        homePageEditText = findViewById(R.id.homePageEditText);
        contactEditText = findViewById(R.id.contactEditText);
        bioEditText = findViewById(R.id.bioEditText);
        changeAvatarTextView = findViewById(R.id.editAvatarTextView);
        saveButton = findViewById(R.id.editProfileButton);
        avatarImageView = findViewById(R.id.avatarImageView);
        deletePic = findViewById(R.id.profilePicDelete);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        changeAvatarTextView.setOnClickListener(v -> openFileChooser());

        db.collection("users").document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String homePage = documentSnapshot.getString("homePage");
                String contact = documentSnapshot.getString("contact");
                String bio = documentSnapshot.getString("bio");

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                homePageEditText.setText(homePage);
                contactEditText.setText(contact);
                bioEditText.setText(bio);

                // Load existing profile image with cache invalidation
                String avatarUrl = documentSnapshot.getString("avatarUrl");
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)  // <- This will force Glide to re-fetch the image
                            .skipMemoryCache(true)  // <- This skips memory cache
                            .into(avatarImageView);
                }
            }
        });

        saveButton.setOnClickListener(v -> {
            String updatedFirstName = firstNameEditText.getText().toString().trim();
            String updatedLastName = lastNameEditText.getText().toString().trim();
            String updatedHomePage = homePageEditText.getText().toString().trim();
            String updatedContact = contactEditText.getText().toString().trim();
            String updatedBio = bioEditText.getText().toString().trim();

            if (imageUri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars/" + deviceId);
                storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profileImageUrl = uri.toString();
                        db.collection("users").document(deviceId)
                                .update("firstName", updatedFirstName, "lastName", updatedLastName, "homePage", updatedHomePage, "contact", updatedContact, "bio", updatedBio, "avatarUrl", profileImageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    // After successful upload and Firestore update, reload the image with updated URL
                                    Glide.with(this)
                                            .load(profileImageUrl)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)  // This forces Glide to re-fetch the image
                                            .skipMemoryCache(true)  // This skips the memory cache
                                            .into(avatarImageView);
                                    finish(); // Successfully updated profile, go back to the Profile fragment
                                })
                                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
                    });
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
            } else {
                db.collection("users").document(deviceId)

                        .update("firstName", updatedFirstName, "lastName", updatedLastName, "homePage", updatedHomePage, "contact", updatedContact, "bio", updatedBio)
                        .addOnSuccessListener(aVoid -> onBackPressed())
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });

        deletePic.setOnClickListener(v -> {
            String newAvatarUrl = "https://firebasestorage.googleapis.com/v0/b/quemeful-qr-8e3a2.appspot.com/o/avatars%2Fe4a01d113899a8fc?alt=media&token=33555359-6857-41a5-9078-eaff94876979";

            if (deviceId != null) {
                db.collection("users").document(deviceId).update("avatarUrl", newAvatarUrl)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("EditProfileActivity", "Profile picture successfully updated for user: " + deviceId);
                            Glide.with(this).load(newAvatarUrl).into(avatarImageView);
                        })
                        .addOnFailureListener(e -> Log.e("EditProfileActivity", "Error deleting profile picture", e));
            }
        });

        ImageView backButton = findViewById(R.id.backArrowImageView);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    /**
     * This method is used to select picture/image.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getContent.launch(Intent.createChooser(intent, "Select Picture"));
    }


}



