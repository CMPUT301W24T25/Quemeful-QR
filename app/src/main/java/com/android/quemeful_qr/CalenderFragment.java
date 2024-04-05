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
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalenderFragment extends Fragment {
    private CalendarView mCalendarView;
    private TextView mSelectedDate;
    private SimpleDateFormat dateFormat;
    private SharedViewModel sharedViewModel;

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

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Initialize the CalendarView to display today's date
        Calendar today = Calendar.getInstance();
        mCalendarView.setDate(today.getTimeInMillis());
        updateSelectedDate(today);

        mCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            updateSelectedDate(selectedDate);
            sharedViewModel.selectDate(selectedDate); // Share the selected date with the ViewModel
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
