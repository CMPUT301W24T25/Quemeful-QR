    package com.android.quemeful_qr;

    import static android.content.ContentValues.TAG;

    import android.annotation.SuppressLint;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.Build;
    import android.os.SystemClock;
    import android.util.Log;
    import android.widget.RemoteViews;

    import androidx.annotation.NonNull;
    import androidx.core.app.NotificationCompat;

    import com.google.firebase.messaging.FirebaseMessagingService;
    import com.google.firebase.messaging.RemoteMessage;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.Map;

    /**
     * This is a service class responsible for handling Firebase Cloud Messaging (FCM) notifications.
     * It extends FirebaseMessagingService to receive and process incoming messages.
     * Reference URL- https://www.youtube.com/watch?v=2xoJi-ZHmNI&t=1915s&ab_channel=GeeksforGeeks
     * Author- GeeksforGeeks, Published Date- 30 Aug, 2021
     */
    public class ReceiveNotifications extends FirebaseMessagingService {

        /**
         * This method is called when a new token is generated for the device.
         * @param token The new token generated for the device.
         */
        @Override
        public void onNewToken(@NonNull String token) {
            super.onNewToken(token);
        }

        /**
         * This method is called when a new FCM message is received.
         * @param message The incoming FCM message.
         */
        @Override
        public void onMessageReceived(@NonNull RemoteMessage message) {
            if (message.getNotification() != null) {
                String Name = message.getNotification().getTitle();
                String title = message.getNotification().getBody();

                int icon = Integer.getInteger( message.getNotification().getIcon());
                handleMessage(Name, title, icon);
                Log.d(TAG, "onMessageReceived: " + title + " " );

            }
        }

        /**
         * This method is used to construct a RemoteViews object for the custom notification layout.
         * @param title The title of the notification.
         * @param message The body/message of the notification.
         * @return A RemoteViews object containing the custom notification layout.
         */
        public RemoteViews getRemoteView(String title, String message) {
            @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notification_title, title);
            remoteViews.setTextViewText(R.id.notification_description, message);

            return remoteViews;
        }

        /**
         * This method is used to handle the incoming FCM message by creating and displaying a custom notification.
         * @param title The title of the notification.
         * @param message The body/message of the notification.
         */
        public void handleMessage(String title,String  message, int icon) {
            String CHANNEL_ID = "MESSAGE";
            String channelName = "NOTIFICATION MESSAGE";
            Intent intent = new Intent(getApplicationContext(), ShowNotificationsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );


            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(icon).setContentTitle(title).setContentText(message)
                    .setAutoCancel(true).setOnlyAlertOnce(true).setContentIntent(pendingIntent);
            notification = notification.setContent(getRemoteView(title, message));

            NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }


                notification.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);

            notificationManager.notify(0, notification.build());
        }



    } // class closing
