package com.android.quemeful_qr;

import static org.junit.Assert.*;

import com.android.quemeful_qr.DateUtils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilsTest {

    @Test
    public void testParseDate() {
        String inputDate = "2023-03-15";
        Date result = DateUtils.parseDate(inputDate);
        assertNotNull("The parsed date should not be null", result);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(result);
        assertEquals("The formatted date should match the input date", inputDate, formattedDate);
    }


    @Test
    public void testIsToday() {
        // Test with today's date
        Date today = new Date();
        assertTrue("isToday should return true for today's date", DateUtils.isToday(today));

        // Test with yesterday's date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();
        assertFalse("isToday should return false for yesterday's date", DateUtils.isToday(yesterday));
    }
}