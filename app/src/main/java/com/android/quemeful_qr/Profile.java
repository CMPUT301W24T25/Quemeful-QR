package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class Profile extends Fragment {

    private TextView firstNameTextView;
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

    private void fetchProfileInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(deviceId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String avatarPath = documentSnapshot.getString("avatarUrl");

                firstNameTextView.setText(String.format("%s %s", firstName, lastName));

                if (avatarPath != null && !avatarPath.isEmpty()) {
                    if (avatarPath.startsWith("http") || avatarPath.startsWith("https")) {
                        // Avatar path is a remote URL
                        Glide.with(this).load(avatarPath).into(avatarImageView);
                    } else {
                        // Avatar path is a local file path
                        File imgFile = new File(avatarPath);
                        if (imgFile.exists()) {
                            Drawable drawable = Drawable.createFromPath(imgFile.getAbsolutePath());
                            avatarImageView.setImageDrawable(drawable);
                        }
                    }
                }
            }
        });
    }
}
