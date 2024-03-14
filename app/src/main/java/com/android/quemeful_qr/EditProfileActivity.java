package com.android.quemeful_qr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private TextView changeAvatarTextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        changeAvatarTextView = findViewById(R.id.editAvatarTextView);
        saveButton = findViewById(R.id.editProfileButton);
        avatarImageView = findViewById(R.id.avatarImageView);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        changeAvatarTextView.setOnClickListener(v -> openFileChooser());

        db.collection("users").document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);

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

            if (imageUri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatars/" + deviceId);
                storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profileImageUrl = uri.toString();
                        db.collection("users").document(deviceId)
                                .update("firstName", updatedFirstName, "lastName", updatedLastName, "avatarUrl", profileImageUrl)
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
                        .update("firstName", updatedFirstName, "lastName", updatedLastName)
                        .addOnSuccessListener(aVoid -> finish()) // No new image to upload; just finish activity
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });

        ImageView backButton = findViewById(R.id.backArrowImageView);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getContent.launch(Intent.createChooser(intent, "Select Picture"));
    }
}
