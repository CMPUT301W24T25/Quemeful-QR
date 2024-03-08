package com.android.quemeful_qr;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
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

/**
 * A Fragment class representing an announcement posting feature.
 * Users can input a title and description for an announcement and post it, triggering a notification to be sent to a specific topic.
 */
public class announcement extends Fragment {
    /**
     * The ID of the event/topic to which the notification will be sent.
     */
    private String Eventid;
    /**
     * Constructs a new instance of the announcement Fragment with the specified event ID.
     * @param Eventid The ID of the event/topic to which the notification will be sent.
     */
    public announcement(String Eventid) {
        this.Eventid = Eventid;
    }
    /**
     * Creates a new instance of the announcement Fragment with the specified event ID.
     * @param Eventid The ID of the event/topic to which the notification will be sent.
     * @return A new instance of the announcement Fragment.
     */
    public static announcement newInstance(String Eventid) {
        return new announcement(Eventid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * Called to create the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState This fragment's previously saved state, if any.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        TextInputEditText titleTextInput = view.findViewById(R.id.Title_text_input);
        TextInputEditText descriptionTextInput = view.findViewById(R.id.descriptionTextInput);

        Button post = view.findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTextInput.getText().toString();
                String description = descriptionTextInput.getText().toString();
                sendNotification(title,description);
                titleTextInput.getText().clear();
                descriptionTextInput.getText().clear();

            }
        });

        ImageButton back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }
    /**
     * Sends a notification with the specified title and description to the event/topic.
     * @param title The title of the notification.
     * @param descrtiption The description/body of the notification.
     */
    /**
    *https://www.youtube.com/watch?v=YjNZO90yVsE&t=530s&ab_channel=EasyTuto
    */
    void sendNotification(String title,String descrtiption){
        try{
            JSONObject jsonObject = new JSONObject();

            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body",descrtiption);

            jsonObject.put("notification",notification);
            jsonObject.put("to","/topics/"+Eventid);
            callApi(jsonObject);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Calls the API to send the notification using the provided JSON object.
     * @param jsonObject The JSON object containing the notification details.
     */
    void callApi(JSONObject jsonObject){
        FirebaseFirestore.getInstance().collection("notification").document("key").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String key = document.getString("key");
                            Log.d("TAG", "Key: " + key);
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
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("TAG", "Response: " + responseBody);
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Notification sent successfully", Toast.LENGTH_SHORT).show());

                } else {
                    Log.e("TAG", "Error: " + response.code() + " " + response.message());
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "An Error Occurred. Please try again later.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
                    }
                });
    }


}
