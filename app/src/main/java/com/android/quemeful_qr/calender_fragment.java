package com.android.quemeful_qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class calender_fragment extends Fragment {
    private CalendarView mCalendarView;
    private TextView mSelectedDate;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.calender, container, false);

        mCalendarView = rootView.findViewById(R.id.calendarView);
        mSelectedDate = rootView.findViewById(R.id.selectedDate);

        // Set the date format to "dd MMMM yyyy"
        dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        // Initialize the CalendarView to display today's date
        Calendar today = Calendar.getInstance();
        // Set today's date in the CalendarView
        mCalendarView.setDate(today.getTimeInMillis());
        // Update the selected date TextView with today's date
        updateSelectedDate(today);

        // Set the date change listener
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                // Update the selected date TextView with the selected date
                updateSelectedDate(selectedDate);
            }
        });

        return rootView;
    }

    private void updateSelectedDate(Calendar date) {
        // Format the date to display in the TextView
        String formattedDate = dateFormat.format(date.getTime());
        // Set the formatted date in the TextView
        mSelectedDate.setText(formattedDate);
    }
}
