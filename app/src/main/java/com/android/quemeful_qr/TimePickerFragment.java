//https://stackoverflow.com/a/2660148
//https://www.youtube.com/watch?v=QMwaNN_aM3U
package com.android.quemeful_qr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    interface TimePickerDialogListener{
        void setTimeClickFalse();

    }
    private TimePickerFragment.TimePickerDialogListener listener;
    @Override
    public void onAttach(@NonNull Context context){
//        this will help set the buttons
        super.onAttach(context);
        if(context instanceof DatePickerFragment.DatePickerDialogListener){
            listener = (TimePickerFragment.TimePickerDialogListener) context;
        } else {
            throw new RuntimeException(context + "must implement TimePickerDialogListener");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);




        TimePickerDialog timePicker = new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));

        timePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                if (which == DialogInterface.BUTTON_NEGATIVE){
                    listener.setTimeClickFalse();
                }

            }
        });


        return timePicker;

    }
}
