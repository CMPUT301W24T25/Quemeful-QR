package com.android.quemeful_qr;

import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hmmaa", Locale.getDefault());
    public static Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Date parseTime(String timeString){
        try {
            return timeFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);
        return today.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR);
    }
}

