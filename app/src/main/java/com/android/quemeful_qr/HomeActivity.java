//https://www.youtube.com/watch?v=DOd6E6azS5I
package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements
        AddEventFragment.AddEventDialogListener {
    //toolbar buttons
    private ImageView backIcon;
    private ImageView menuIcon;
    private TextView title;
    //navbar buttons
    private ImageView homeIcon;
    private ImageView calendarIcon;
    private ImageView profileIcon;

    //listview buttons
    private ArrayList<Event> dataList;
    private ArrayList<Event> attendList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;

    private EditText input;
    private Button deleteall;
    private Button confirm;
    private Button delete;
    private Button edit;

    private Button checkin;
    private Button viewbutton;
    private Button addevent;
    private int listposition;
    private Event event;
    private RelativeLayout inputfield;

    private boolean editbool;

    private int eventcount;

    private int attendingcount;


    public void addEvent(Event event){ // define functionality for the implements
        eventAdapter.add(event); //
        eventAdapter.notifyDataSetChanged();
    }
    public void editEvent(Event event){

        eventAdapter.insert(event, listposition);
        eventAdapter.notifyDataSetChanged();
    }

    public void removeEvent(){
        dataList.remove(listposition);
        event = null;
        eventAdapter.notifyDataSetChanged();
    }

    public void clearSelection(){
        eventList.clearChoices();
        event = null;
        eventAdapter.notifyDataSetChanged();
        eventList.setAdapter(eventAdapter);
    }
    public boolean getEditbool() {
        return editbool;
    }

    public void setEditbool(boolean editbool) {
        this.editbool = editbool;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpToolbars();
        setUpNavbars();
        setUpInterface();

        String[] events = {

        };
        String[] eventUUIDs = {

        };

//        boolean[] read_statuses = {
//
//        };

        dataList = new ArrayList<Event>();
        for (int i = 0; i < events.length; i++){
            dataList.add(new Event(eventUUIDs[i], events[i], "imageuri"));
        }



        eventList = findViewById(R.id.event_list); //list from view in xml
        eventAdapter = new EventArrayAdapter(this,dataList); // adapter connects them
        eventList.setAdapter(eventAdapter);

        this.updateEventCount();
//        this.updateAttendingCount();

        FloatingActionButton fab = findViewById(R.id.add_floating_button);
        fab.setOnClickListener(v -> {
            if(editbool == false) {
                new AddEventFragment().show(getSupportFragmentManager(), "Add Event"); //brings up the fragment pop up window
            }
            else{
                editbool = false;
                clearSelection();
                new AddEventFragment().show(getSupportFragmentManager(), "Add Event");

            }
        });


        input = findViewById(R.id.input);
        deleteall = findViewById(R.id.delete_all_button);
        confirm = findViewById(R.id.confirm_button);
        delete = findViewById(R.id.delete_button);
        edit = findViewById(R.id.edit_button);
        checkin = findViewById(R.id.checkin_button);
        viewbutton = findViewById(R.id.view_button);
        addevent = findViewById(R.id.addevent_button);


        deleteall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                event = null;
                inputfield = (RelativeLayout) findViewById(R.id.field);
                inputfield.setVisibility(View.VISIBLE);
                clearSelection();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String text = input.getText().toString();
                if (text.equals("delete")){
                    removeAll(dataList);
                    Toast myToast = Toast.makeText(HomeActivity.this, "Deleted all events", Toast.LENGTH_SHORT);
                    myToast.show();

                    event = null;
                    editbool = false;
                    clearCount();
                    RelativeLayout inputfield = (RelativeLayout)findViewById(R.id.field);
                    inputfield.setVisibility(View.INVISIBLE);
                } else {
                    Toast myToast = Toast.makeText(HomeActivity.this, "Type 'delete' to confirm", Toast.LENGTH_SHORT);
                    myToast.show();

                }

            }

        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                editbool = true;
                event = dataList.get(i);
                listposition = i;
                Toast myToast = Toast.makeText(HomeActivity.this, "Selected " + event.getEventName(), Toast.LENGTH_SHORT);
                myToast.show();

            }


        });

        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(event == null){
                    Toast myToast = Toast.makeText(HomeActivity.this, "Select an event", Toast.LENGTH_SHORT);
                    myToast.show();
                }else {
//
                    AddEventFragment newFragment = AddEventFragment.newInstance(event);
                    newFragment.show(getSupportFragmentManager(), "Edit Book");
                    Toast myToast = Toast.makeText(HomeActivity.this, "Edit " + event.getEventName(), Toast.LENGTH_SHORT);
                    myToast.show();
                    clearSelection();

                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(event == null){
                    Toast myToast = Toast.makeText(HomeActivity.this, "Select an event", Toast.LENGTH_SHORT);
                    myToast.show();
                }else {
                    removeItem(listposition);
                    Toast myToast = Toast.makeText(HomeActivity.this, "Deleted " + event.getEventName(), Toast.LENGTH_SHORT);
                    myToast.show();
                    updateEventCount();
//                    updateAttendingCount();
                    clearSelection();
                    event = null;
                }
            }
        });

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRCheckActivity();
            }
        });

        addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddEventActivity();
            }
        });

        viewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewEventActivity();
            }
        });




    }
    public void removeItem(int pos){
        dataList.remove(pos);
        eventList.setAdapter(eventAdapter);
    }
    public void removeAll(ArrayList<Event> arraylist){
        arraylist.clear();
        eventList.setAdapter(eventAdapter);
    }

    public void updateEventCount(){
        eventcount = dataList.size();
        TextView textEventCount = findViewById(R.id.event_count);
        textEventCount.setText("total count: " + eventcount);
        eventList.setAdapter(eventAdapter);
    }
    public void clearCount(){
        dataList.removeAll(dataList);
//        attendList.removeAll(attendList);
//        attendingcount = attendList.size();
//        TextView textAttendingCount = findViewById(R.id.content_attending_check);
//        textAttendingCount.setText("attending count: " + textAttendingCount);

        eventcount = dataList.size();
        TextView textEventCount = findViewById(R.id.event_count);
        textEventCount.setText("event count: "+ eventcount);
        eventList.setAdapter(eventAdapter);

    }
//    public void updateReadCount(){
//        eventList.removeAll(eventList);
//        for(Event event:dataList) {
//            if (event.isAttending_status() == true){  //event not in eventlist yet
//                attendList.add(event);
//            }
//        }
//        attendingcount = attendList.size();
//        TextView textReadCount = findViewById(R.id.attending_count);
//        textReadCount.setText("attending count: " + attendingcount);
//        eventList.setAdapter(eventAdapter);
//    }
//tool and nav bar methods

    protected void openMainActivity(){
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    protected void openCalendarActivity(){
        Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
        startActivity(intent);
    }
    protected void openProfileActivity(){
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
    protected void openQRCheckActivity(){
        Intent intent = new Intent(HomeActivity.this, QRCheckActivity.class);
        startActivity(intent);
    }
    protected void openAddEventActivity(){
        Intent intent = new Intent(HomeActivity.this, AddEventActivity.class);
        startActivity(intent);
    }
    protected void openViewEventActivity(){
        Intent intent = new Intent(HomeActivity.this, ViewEventActivity.class);
        startActivity(intent);
    }
    protected void setUpInterface(){
    }
    protected void setUpToolbars(){
        backIcon = (ImageView) findViewById(R.id.backarrow_icon);
        menuIcon = (ImageView) findViewById(R.id.menu_icon);
        title =  (TextView) findViewById(R.id.toolbar_title);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(HomeActivity.this, "back icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(HomeActivity.this, "menu icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
    }
    protected void setUpNavbars(){

        homeIcon = (ImageView) findViewById(R.id.home_icon);
        calendarIcon = (ImageView) findViewById(R.id.calendar_icon);
        profileIcon = (ImageView) findViewById(R.id.profile_icon);


        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast mytoast = Toast.makeText(HomeActivity.this, "home icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarActivity();
                Toast mytoast = Toast.makeText(HomeActivity.this, "calendar icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
                Toast mytoast = Toast.makeText(HomeActivity.this, "profile icon clicked", Toast.LENGTH_SHORT);
                mytoast.show();
            }
        });



    }

}