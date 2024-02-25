package com.android.quemeful_qr;// getNotification.java

import com.google.firebase.messaging.FirebaseMessaging;

public class sign_up_to_notificaition {
    public sign_up_to_notificaition(String Eventid) {

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic(Eventid);
    }




}
