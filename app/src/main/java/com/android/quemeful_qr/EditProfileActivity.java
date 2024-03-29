package com.android.quemeful_qr;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
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
                    loadFromUri(imageUri); // Load and display the new image based on its type
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

                String avatarUrl = documentSnapshot.getString("avatarUrl");
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    loadFromUri(Uri.parse(avatarUrl));
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
                                    loadFromUri(Uri.parse(profileImageUrl)); // Reload using the correct format
                                    onBackPressed();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
                    });
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show());
            } else {
                db.collection("users").document(deviceId)
                        .update("firstName", updatedFirstName, "lastName", updatedLastName)
                        .addOnSuccessListener(aVoid -> onBackPressed())
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
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

    /**
     * This method is used to load the selected image from uri and display it in the imageview.
     * @param uri the selected image's URI.
     */
    private void loadFromUri(Uri uri) {
        if (uri.toString().contains("avataaars.io")) {
            loadSvgFromUrl(uri.toString());
        } else {
            String fileExtension = getFileExtension(uri);
            if ("svg".equalsIgnoreCase(fileExtension)) {
                loadSvgFromUrl(uri.toString());
            } else {
                Glide.with(this).load(uri).into(avatarImageView);
            }
        }
    }

    /**
     * This method is used to fetch the file extension from uri of the selected image.
     * It works by splitting the uri at '/' into an array then fetches the remaining part.
     * @param uri the selected image's URI.
     * @return String
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        String type = contentResolver.getType(uri);
        if (type != null) {
            return type.split("/")[1];
        }
        return null;
    }

    /**
     * This method is used to retrieve the image in SVG format from its url.
     * @param url the url of the image in svg format.
     */
    private void loadSvgFromUrl(String url) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                InputStream inputStream = connection.getInputStream();
                SVG svg = SVG.getFromInputStream(inputStream);
                final PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                runOnUiThread(() -> avatarImageView.setImageDrawable(drawable));
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }
        }).start();
    }
} // class closing
