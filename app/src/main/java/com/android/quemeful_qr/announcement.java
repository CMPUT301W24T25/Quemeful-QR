package com.android.quemeful_qr;


import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.compose.ui.window.Notification;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class announcement extends Fragment {

    private String Eventid;

    public announcement(String Eventid) {
        this.Eventid = Eventid;
    }

    public static announcement newInstance(String Eventid) {
        return new announcement(Eventid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        return view;
    }

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


    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "key=")
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
