//https://stackoverflow.com/a/10209902
package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is used to handle user profile's editing functionality.
 */
public class Profile extends Fragment {

    TextView firstNameTextView;
    TextView homePageTextView;
    TextView contactTextView;
    TextView bioTextView;

    private ImageView avatarImageView;
    private String deviceId;

    /**
     * Profile default constructor (no parameters)
     */
    public Profile() {
        // Required empty public constructor
    }

    /**
     * This method is used to create a new instance of the Profile class.
     * @return a new Profile instance
     */
    public static Profile newInstance() {
        return new Profile();
    }

    /**
     * This method is used to create the view for user profile editing.
     * It retrieves the user by its device id and sets a listener on the edit profile button,
     * which starts the EditProfileActivity.java.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        homePageTextView = view.findViewById(R.id.homePageTextView);
        contactTextView = view.findViewById(R.id.contactTextView);
        bioTextView = view.findViewById(R.id.bioTextView);
        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        avatarImageView = view.findViewById(R.id.avatarImageView);
        Switch geolocationSwitch = view.findViewById(R.id.notificationSwitch);
        Button showNotificationsButton = view.findViewById(R.id.Notification_button);

        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        fetchProfileInfo();

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        geolocationSwitch.isChecked();



        showNotificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ShowNotificationsActivity.class);
            startActivity(intent);
        });





        return view;
    }

    /**
     * This onResume method is used to call the method for fetching the updated profile details, on resuming.
     */
    @Override
    public void onResume() {
        super.onResume();
        fetchProfileInfo();
    }

    /**
     * This method is used to fetch user profile details from the firebase and,
     * also to load the avatar/profile picture into the ImageView.
     */
    private void fetchProfileInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String avatarUrl = documentSnapshot.getString("avatarUrl");
                String homePage = documentSnapshot.getString("homePage");
                String contact = documentSnapshot.getString("contact");
                String bio = documentSnapshot.getString("bio");

                firstNameTextView.setText(String.format("%s %s", firstName, lastName));
                homePageTextView.setText(String.format("%s", homePage));
                contactTextView.setText(String.format("%s", contact));
                bioTextView.setText(String.format("%s", bio));

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    if (avatarUrl.endsWith(".svg") || avatarUrl.contains("avataaars.io")) {
                        loadSvgFromUrl(avatarUrl);
                    } else {
                        Glide.with(this).load(avatarUrl).into(avatarImageView);
                    }
                }
            }
        });
    }

    /**
     * This method is used to convert the image url to SVG and display it in the imageview.
     * @param url the url of the image
     */
    private void loadSvgFromUrl(String url) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                InputStream inputStream = connection.getInputStream();
                SVG svg = SVG.getFromInputStream(inputStream);
                final PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> avatarImageView.setImageDrawable(drawable));
                }
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }
        }).start();
    }
} // class closing