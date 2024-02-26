package com.android.quemeful_qr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.UUID;

public class AddEventFragment extends DialogFragment {
    /* new code */
    interface AddEventDialogListener{ //define certain functions
        void addEvent(Event event);
        void editEvent(Event event);

        void removeEvent();
        boolean getEditbool();
        void setEditbool(boolean editbool);

        void clearSelection();

        void updateEventCount();
//        void updateAttendingCount();


    }
    private AddEventDialogListener listener;
    //    attach properly to the main activity
    @Override
    public void onAttach(@NonNull Context context){
//        this will help set the buttons
        super.onAttach(context);
        if(context instanceof AddEventDialogListener){
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + "must implement AddEventDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
//        grab view from xml file and connect it to java
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_event,null);
        EditText editEventName = view.findViewById(R.id.fragment_event_name);
        CheckBox editAttendingStatus= view.findViewById(R.id.fragment_attending_check);

        if (listener.getEditbool() == true) {
            Event event = (Event) getArguments().getSerializable("event");
            if (getArguments() != null) {
                editEventName.setText(event.getEventName());
            }
        }


//create pop up on screen
// Dialog fragment type is an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder //default has title and negative/pos button (pop up window)
                .setView(view) //set the builder to have the view grabbed from the xml file
                .setTitle("Add/edit event")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    if (listener.getEditbool() == true){
                        listener.clearSelection();
                        listener.setEditbool(false);
                    } else {
                        listener.clearSelection();

                    }
                }) // negativeButton = cancel or close

                .setPositiveButton("Ok", (dialog, which) -> {


                    if (listener.getEditbool() == false){
                        String listEventName = editEventName.getText().toString();
//                        boolean attending_status = editReadStatus.isChecked();
                        String listEventUUID = UUID.randomUUID().toString();
                        listener.addEvent(new Event(listEventUUID, listEventName,"imageuri"));
                        listener.updateEventCount();
//                        listener.updateAttendingCount();
                        listener.clearSelection();

                    }
                    if (listener.getEditbool() == true){
                        String listEventName = editEventName.getText().toString();

//                        boolean attending_status = editAttendingStatus.isChecked();
                        String listEventUUID = UUID.randomUUID().toString();
                        listener.removeEvent();
                        listener.editEvent(new Event(listEventUUID, listEventName, "imageuri"));
                        listener.setEditbool(false);
//                        listener.updateAttendingCount();
                        listener.clearSelection();

                    }

                })
                .create(); //create popup
    }
    static AddEventFragment newInstance(Event event){
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        AddEventFragment fragment = new AddEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

}