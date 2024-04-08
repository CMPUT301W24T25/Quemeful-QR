package com.android.quemeful_qr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowNotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_show_notifications_list);

        recyclerView = findViewById(R.id.Show_Notification_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        loadNotifications();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadNotifications() {
        String currentUserUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
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
                                        String from = (String) eventDocument.get("title");
                                        List<Map<String, String>> eventNotifications = (List<Map<String, String>>) eventDocument.get("notifications");

                                        if (eventNotifications != null) {
                                            for (Map<String, String> notificationDocument : eventNotifications) {
                                                if (notificationDocument != null) {
                                                    String title = notificationDocument.get("title");
                                                    String body = notificationDocument.get("body");

                                                    String date_time = notificationDocument.get("date");
                                                    notifications.add(new Notification(title, body, from, date_time, image));
                                                }
                                            }
                                        }
                                        notifications.sort(new Comparator<Notification>() {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);

                                            @Override
                                            public int compare(Notification notification1, Notification notification2) {
                                                try {
                                                    Date date1 = dateFormat.parse(notification1.getDate_time());
                                                    Date date2 = dateFormat.parse(notification2.getDate_time());
                                                    // Compare dates
                                                    return date2.compareTo(date1);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                    return 0; // Handle parsing exception
                                                }
                                            }
                                        });

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
