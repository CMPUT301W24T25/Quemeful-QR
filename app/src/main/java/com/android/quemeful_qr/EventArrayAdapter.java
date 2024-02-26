package com.android.quemeful_qr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }
    // need to override the getView (renders the row of listview, this is called for every item in list view)
    // shows the output on the screen
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view; //override view with the one in content.xml file
        if (convertView == null){ // called when launching the app for first time
            view = LayoutInflater.from(getContext()).inflate(R.layout.eventlistcontents, parent, false);
        } else { // already have a view on screen but when scrolling then we reuse the "field of view" to see the visible items
            view = convertView; //convertView reuses views that goes out of screen
        }

        Event event = getItem(position); //loops through each row in the listview and renders and shows the data for each row
        TextView event_name = view.findViewById(R.id.content_event_name);
//        TextView attending_check = view.findViewById(R.id.content_attending_check);
//
        event_name.setText(event.getEventName());
//        if (event.isAttending_status() == true){
//            attending_check.setVisibility(View.VISIBLE);
//        }
//        else{
//            attending_check.setVisibility(View.INVISIBLE);
//        }
        return view; //shows the view
    }
}
