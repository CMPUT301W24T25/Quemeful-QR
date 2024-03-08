
package com.android.quemeful_qr;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/**
 * A service class responsible for handling Firebase Cloud Messaging (FCM) notifications.
 * It extends FirebaseMessagingService to receive and process incoming messages.
 */
public class recieveNotification extends FirebaseMessagingService {
    /**
     * Called when a new token is generated for the device.
     * @param token The new token generated for the device.
     */
    @Override
    public void onNewToken(@NonNull String token) {

        super.onNewToken(token);
    }

    /**
     * Called when a new FCM message is received.
     * @param message The incoming FCM message.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getNotification() != null) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            handleMessage(title, body);
        }
    }
    /**
     * Constructs a RemoteViews object for the custom notification layout.
     * @param title The title of the notification.
     * @param message The body/message of the notification.
     * @return A RemoteViews object containing the custom notification layout.
     */
    public RemoteViews getRemoteView(String title, String message) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.notificaiton_title, title);
        remoteViews.setTextViewText(R.id.notificaiton_description, message);
        remoteViews.setImageViewResource(R.id.notification_image,R.drawable.qrcode_solid);
        return remoteViews;
    }

    /**
     * Handles the incoming FCM message by creating and displaying a custom notification.
     * @param title The title of the notification.
     * @param message The body/message of the notification.
     */
    public void handleMessage(String title,String  message) {
        String CHANNEL_ID = "MESSAGE";
        String channename = "NOTIFICATION MESSAGE";
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.qrcode_solid)
                .setAutoCancel(true).setOnlyAlertOnce(true).setContentIntent(pendingIntent);
        notification = notification.setContent( getRemoteView(title, message));

        NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channename, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notification.build());
    } }