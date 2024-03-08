//https://stackoverflow.com/a/7030149
package com.android.quemeful_qr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * contains pop up window that allows user to pick a date from a calendar
 */
public class DatePickerFragment extends DialogFragment{
    // Use the current date as the default date in the picker.
    interface DatePickerDialogListener{
       void setDateClickFalse();

    }
    private DatePickerDialogListener listener;

    /**
     * attaches the fragment to the host activity
     * DatePickerDialogListener listener is started so functions from other classes can be used here
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context){
//        this will help set the buttons
        super.onAttach(context);
        if(context instanceof DatePickerDialogListener){
            listener = (DatePickerDialogListener) context;
        } else {
            throw new RuntimeException(context + "must implement DatePickerDialogListener");
        }
    }

    /**
     * Create a new instance of DatePickerDialog and return it.
     * If user clicks cancel, clears boolean values that shows which textview is pressed
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);

        datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                if (which == DialogInterface.BUTTON_NEGATIVE){
                    listener.setDateClickFalse();
                }

            }
        });


        return datePicker;
    }

}
