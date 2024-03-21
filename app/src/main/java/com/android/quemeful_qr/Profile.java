package com.android.quemeful_qr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Profile extends Fragment {

    TextView firstNameTextView;
    TextView homePageTextView;
    TextView contactTextView;
    private Button editProfileButton;
    private ImageView avatarImageView;
    private String deviceId;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        homePageTextView = view.findViewById(R.id.homePageTextView);
        contactTextView = view.findViewById(R.id.contactTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        avatarImageView = view.findViewById(R.id.avatarImageView);

        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        fetchProfileInfo();

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfileInfo();
    }

    /**
     * get profile info from firebase
     * load the picture into an imageview
     */
    private void fetchProfileInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String avatarUrl = documentSnapshot.getString("avatarUrl");

                firstNameTextView.setText(String.format("%s %s", firstName, lastName));

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
     * take url convert to svg and put it onto imageview
     * @param url
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
}
