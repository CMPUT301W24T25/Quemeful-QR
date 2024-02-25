package com.android.quemeful_qr;


import android.annotation.SuppressLint;
import android.os.StrictMode;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "BD8WwilMrfBaIsi6c-4g8ouy5laaSIbdSi-4HbGvNDkL2cRkpWPqvEHvTAeHQPta-ztYcer5lcd0HOruduxdeu8 ";

    public static void SetServerKey(String serverKey) {
        FCMSend.SERVER_KEY = "key=" + serverKey;
    }

    protected String title = null, body = null, to = null;
    protected boolean topic;
    protected String result;

    public String Result() {
        return result;
    }

    public static class Builder {
        private FCMSend mFcm;

        public Builder(String to) {
            mFcm = new FCMSend();
            mFcm.to = to;
        }

        public Builder(String to, boolean topic) {
            mFcm = new FCMSend();
            mFcm.to = to;
            mFcm.topic = topic;
        }

        public Builder setTitle(String title) {
            mFcm.title = title;
            return this;
        }

        public Builder setBody(String body) {
            mFcm.body = body;
            return this;
        }


        @SuppressLint("NewApi")
        public FCMSend send() {
            if (SERVER_KEY == null) mFcm.result = "No Server Key";
            else {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    JSONObject json = new JSONObject();
                    if (mFcm.topic) json.put("to", "/topics/" + mFcm.to);
                    else json.put("to", mFcm.to);
                    JSONObject notification = new JSONObject();
                    notification.put("title", mFcm.title);
                    notification.put("body", mFcm.body);

                    HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json;");
                    conn.setRequestProperty("Authorization", SERVER_KEY);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    OutputStream os = conn.getOutputStream();
                    os.write(json.toString().getBytes("UTF-8"));
                    os.close();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String result = IOUtils.toString(in, "UTF-8");
                    in.close();
                    conn.disconnect();
                    mFcm.result = result;
                } catch (JSONException | IOException e) {
                    mFcm.result = e.getMessage();
                }
            }
            return mFcm;
        }
    }
}
