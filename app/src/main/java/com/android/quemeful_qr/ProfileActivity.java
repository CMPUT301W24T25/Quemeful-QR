package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    ImageView backIcon;
    ImageView menuIcon;
    TextView title;
    ImageView homeIcon;
    ImageView calendarIcon;
    ImageView profileIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbars();
        setUpNavbars();
//        setUpInterface();
    }
    protected void openMainActivity(){
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    protected void openCalendarActivity(){
        Intent intent = new Intent(ProfileActivity.this, CalendarActivity.class);
        startActivity(intent);
    }
    protected void openProfileActivity(){
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    protected void setUpToolbars(){
        backIcon = findViewById(R.id.backarrow_icon);
        menuIcon = findViewById(R.id.menu_icon);
        title = findViewById(R.id.toolbar_title);
        backIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openMainActivity();
                Toast mytoast = Toast.makeText(ProfileActivity.this, "back icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(ProfileActivity.this, "menu icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
    }
    protected void setUpNavbars(){

        homeIcon = findViewById(R.id.home_icon);
        calendarIcon = findViewById(R.id.calendar_icon);
        profileIcon = findViewById(R.id.profile_icon);


        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                Toast mytoast = Toast.makeText(ProfileActivity.this, "home icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarActivity();
                Toast mytoast = Toast.makeText(ProfileActivity.this, "calendar icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast mytoast = Toast.makeText(ProfileActivity.this, "profile icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });

    }
}