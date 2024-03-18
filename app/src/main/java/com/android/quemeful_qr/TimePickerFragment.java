package com.android.quemeful_qr;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * This is a fragment class used to create a pop up window that allows user to pick a time from a clock.
 * Reference URLs:
 * https://stackoverflow.com/a/2660148/timepickerdialog-and-am-or-pm
 * Author- CommonsWare, License- CC BY-SA 2.5, Published Date- 17 Apr, 2010
 * https://www.youtube.com/watch?v=QMwaNN_aM3U
 * Author- Coding in Flow, Published Date- 30 Oct, 2017
 */
public class TimePickerFragment extends DialogFragment {

    // interface
    interface TimePickerDialogListener{
        void setTimeClickFalse();

    }
    private TimePickerFragment.TimePickerDialogListener listener;

    /**
     * This method is used to attach the time picker fragment to the host activity.
     * @param context Context
     */
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

    /**
     * This method is used to create a pop up window displaying the clock to select the time in it.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return time picker dialog pop up
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));

        // when clicked 'cancel' button, it clears out the time picked previously by the user.
        timePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                if (which == DialogInterface.BUTTON_NEGATIVE){
                    listener.setTimeClickFalse();
                }
            }
        });
        return timePicker;
    }
} // fragment class closing
