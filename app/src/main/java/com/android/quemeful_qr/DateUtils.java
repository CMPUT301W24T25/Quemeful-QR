//https://stackoverflow.com/questions/22467899/formatting-date-from-date-object-with-only-mm-dd-yyyy
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

    public static String formatTime(String timeString){
        try {
            Date time = timeFormat.parse(timeString);
            String newTime = new SimpleDateFormat("h:mm a").format(time);
            return newTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static String formatDate(String dateString){
        try {
            Date date = dateFormat.parse(dateString);
            String newDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            return newDate;
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

