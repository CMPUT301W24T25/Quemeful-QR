package com.android.quemeful_qr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.quemeful_qr.Notification;
import com.android.quemeful_qr.NotificationAdapter;
import com.android.quemeful_qr.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                                        List<Map<String, String>> eventNotifications = (List<Map<String, String>>) eventDocument.get("notifications");
                                        Collections.reverse(eventNotifications);
                                        for (Map<String, String> notificationDocument : eventNotifications) {
                                            Log.d(TAG, "Notification data: " + notificationDocument);
                                            if (notificationDocument != null) {
                                                String title = notificationDocument.get("title");
                                                String body = notificationDocument.get("body");
                                                String from = notificationDocument.get("Name");
                                                String date_time = notificationDocument.get("date");
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
