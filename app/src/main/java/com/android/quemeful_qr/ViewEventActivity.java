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
    private ImageView backIcon;
    private ImageView menuIcon;
    private TextView title;
    private ImageView posterView;
    private TextView textview_EventName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        setUpToolbars();

        textview_EventName = findViewById(R.id.textview_event_name);
        posterView = findViewById(R.id.poster_view);

        Intent intent = getIntent();
        String poster = intent.getStringExtra("key");
        textview_EventName.setText(poster);
        assert poster != null;
        byte[] imageAsBytes = Base64.decode(poster.getBytes(), Base64.DEFAULT);
        posterView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));


    }
    protected void setUpToolbars(){
        backIcon = (ImageView) findViewById(R.id.backarrow_icon);
        menuIcon = (ImageView) findViewById(R.id.menu_icon);
        title =  (TextView) findViewById(R.id.toolbar_title);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(ViewEventActivity.this, "back icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(ViewEventActivity.this, "menu icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
    }
}