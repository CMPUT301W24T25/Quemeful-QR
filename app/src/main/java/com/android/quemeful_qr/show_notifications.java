package com.android.quemeful_qr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.quemeful_qr.Notification;
import com.android.quemeful_qr.NotificationAdapter;
import com.android.quemeful_qr.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class show_notifications extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    public show_notifications() {
        // Required empty public constructor
    }

    public static show_notifications newInstance() {
        return new show_notifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_notifications_list, container, false);

        recyclerView = view.findViewById(R.id.Show_Notification_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadNotifications();

        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }

    private void loadNotifications() {
        String currentUserUID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUserUID);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();
                if (userDocument.exists()) {
                    List<String> userEvents = (List<String>) userDocument.get("events");
                    Log.d(TAG, "User events: " + userEvents);
                    if (userEvents != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (String eventId : userEvents) {
                            DocumentReference eventDocRef = FirebaseFirestore.getInstance().collection("events").document(eventId);
                            eventDocRef.get().addOnCompleteListener(eventTask -> {
                                if (eventTask.isSuccessful()) {
                                    DocumentSnapshot eventDocument = eventTask.getResult();
                                    if (eventDocument.exists()) {
                                        String image = (String) eventDocument.get("poster");
                                        List<Map<String, String>>eventNotifications = (List<Map<String, String>>) eventDocument.get("notifications");
                                        Collections.reverse( eventNotifications);
                                        for (Map<String, String> notificationDocument : eventNotifications) {
                                            Log.d(TAG, "Notification data: " + notificationDocument);
                                            if (notificationDocument != null) {
                                                String title = (String) notificationDocument.get("title");
                                                String body = (String) notificationDocument.get("body");
                                                String from = (String) notificationDocument.get("Name");
                                                String date_time = (String) notificationDocument.get("date");
                                                Log.d(TAG, "Notification: title=" + title + ", body=" + body + ", from=" + from + ", date_time=" + date_time);
                                                notifications.add(new Notification(title, body, from, date_time, image));
                                            }
                                        }
                                        adapter.setNotifications(notifications);
                                    } else {
                                        Log.d(TAG, "Event document does not exist");
                                    }
                                } else {
                                    Log.e(TAG, "Error getting event document", eventTask.getException());
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "User document does not exist");
                }
            } else {
                Log.e(TAG, "Error getting user document", task.getException());
            }
        });
    }





}



