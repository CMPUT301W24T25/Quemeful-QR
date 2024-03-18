package com.android.quemeful_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import org.osmdroid.views.MapView;

/**
 * This class is used to set up create a new event button and handle the location map.
 */
public class Events extends Fragment {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private Button mapButton;
    Button createEventButton;

    /**
     * Events default constructor (no parameters)
     */
    public Events() {}

    /**
     * This onCreate method is used to set up a create event button,
     * to navigate to the CreateNewEventActivity to create a new event.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_events, container, false);
        // Inflate the layout for this fragment
        createEventButton = view.findViewById(R.id.create_event_button);
        mapButton = view.findViewById(R.id.map_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateNewEventActivity();
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });
        return view;
    }

    /**
     * This method is used to start the CreateNewEventActivity when create event button is clicked.
     */
    protected void openCreateNewEventActivity(){
        Intent intent = new Intent(Events.this.getActivity(), CreateNewEventActivity.class);
        startActivity(intent);
    }

    /**
     * This method is used to start the MapActivity when map/location button is clicked.
     */
    protected void openMapActivity(){
        Intent intent = new Intent(Events.this.getActivity(), MapActivity.class);
        startActivity(intent);
    }

} // class closing


