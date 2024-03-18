package com.android.quemeful_qr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This is a class used to custom fix the date display format.
 * Reference URL- https://stackoverflow.com/questions/22467899/formatting-date-from-date-object-with-only-mm-dd-yyyy
 *
 */
public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * The parseDate method converts string into dates.
     * @param dateString the date in string form
     * @return Date
     */
    public static Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This boolean method is used to check whether the given date is today's date.
     * @param date the given date.
     * @return boolean
     */
    public static boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(date);
        return today.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR);
    }
} // class closing

