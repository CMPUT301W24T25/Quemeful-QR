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

    /**
     * attaches the timepickerfragment to the host activity
     * @param context
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
     * calendar pop up to select dates
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);




        TimePickerDialog timePicker = new TimePickerDialog(getActivity(),(TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));

        timePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener(){
            /**
             * if user clicks cancel it will clear out which time textview is clicked
             * @param dialog the dialog that received the click
             * @param which the button that was clicked (ex.
             *              {@link DialogInterface#BUTTON_POSITIVE}) or the position
             *              of the item clicked
             */
            public void onClick(DialogInterface dialog, int which){
                if (which == DialogInterface.BUTTON_NEGATIVE){
                    listener.setTimeClickFalse();
                }

            }
        });


        return timePicker;

    }
}
