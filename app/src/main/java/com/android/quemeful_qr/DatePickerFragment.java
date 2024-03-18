package com.android.quemeful_qr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

/**
 * This is a fragment class used to create a pop up window that allows user to pick a date from calendar.
 * Reference URL- https://stackoverflow.com/questions/3125879/how-to-make-a-listener-for-a-datepickers-cancel-button-in-android/7030149#7030149
 * Author- Amir, License- CC BY-SA 3.0, Published Date- 29 April, 2018
 */
public class DatePickerFragment extends DialogFragment{
    // Use the current date as the default date in the picker.
    interface DatePickerDialogListener{
       void setDateClickFalse();

    }
    private DatePickerDialogListener listener;

    /**
     * This method is used to attach the fragment to the host activity.
     * DatePickerDialogListener listener is started so functions from other classes can be used here
     * @param context Context
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
     * This method is used to create a new instance of DatePickerDialog and return it.
     * If user clicks cancel, clears boolean values that shows which textview is clicked on.
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly created Fragment.
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
} // class closing
