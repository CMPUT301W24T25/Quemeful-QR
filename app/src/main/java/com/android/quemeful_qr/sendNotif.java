package com.android.quemeful_qr;

import static android.app.PendingIntent.getActivity;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.test.InstrumentationRegistry.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sendNotif {




    /**
     * This method is used to send a notification with the specified title and description to the event/topic.
     * Reference URL- https://www.youtube.com/watch?v=YjNZO90yVsE&t=530s
     * Author- Easy Tuto, Published Date- Jul 5, 2023
     * @param EventName The Name of the Event for the notification.
     * @param description The description/body of the notification.
     */
    void sendNotification(String EventName,String description, String sendID, String icon_id){

        try{
            JSONObject jsonObject = new JSONObject();
            JSONObject notification = new JSONObject();

            notification.put("title",EventName);
            notification.put("body",description);
            Log.d(TAG, "sendNotification: " + icon_id);
            notification.put("icon", icon_id);

            jsonObject.put("notification",notification);

            jsonObject.put("to",sendID);
            callApi(jsonObject);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * This method is used to call the API to send the notification using the provided JSON object.
     * @param jsonObject The JSON object containing the notification details.
     */
    void callApi(JSONObject jsonObject){

        FirebaseFirestore.getInstance().collection("notification").document("key").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String key = document.getString("key");
                            MediaType JSON = MediaType.get("application/json; charset=utf-8");
                            OkHttpClient okHttpClient = new OkHttpClient();
                            String url = "https://fcm.googleapis.com/fcm/send";
                            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .addHeader("Authorization", "key=" + key)
                                    .build();
                            okHttpClient.newCall(request);
                            okHttpClient.newCall(request).enqueue(new Callback() {

                                /**
                                 * This method throws an exception when failed to send notification.
                                 * @param call an instance of Call interface representing a single response pair, cannot be execute twice.
                                 * @param e exception
                                 */
                                @Override
                                public void onFailure(@NonNull Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                /**
                                 * This method notifies user that the notification was sent/ failed to send
                                 * @param call an instance of Call interface representing a single response pair, cannot be execute twice.
                                 * @param response response that the notification was sent.
                                 * @throws IOException throws exception if error occurs while sending notifications
                                 */
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        Log.d("TAG", "Response: " + responseBody);


                                    } else {
                                        Log.e("TAG", "Error: " + response.code() + " " + response.message());

                                    }


                                }
                            });
                        }
                    }
                });

    } // call Api function closing
}
