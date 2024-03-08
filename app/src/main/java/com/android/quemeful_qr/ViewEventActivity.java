package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class ViewEventActivity extends AppCompatActivity {
    private ImageView posterView;
    private TextView textview_EventName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        textview_EventName = findViewById(R.id.textview_event_name);
        posterView = findViewById(R.id.poster_view);

        Intent intent = getIntent();
        EventHelper event = (EventHelper) intent.getSerializableExtra("event");

        assert event != null;
        String poster = event.getPoster();
        String name = event.getTitle();
        String uuid = event.getId();
        textview_EventName.setText(poster);
        assert poster != null;
        byte[] imageAsBytes = Base64.decode(poster.getBytes(), Base64.DEFAULT);
        posterView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

    }

}