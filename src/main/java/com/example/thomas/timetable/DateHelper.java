package com.example.thomas.timetable;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Arrays;

class DateHelper {

    // Int for different weekdays
    final static int MONDAY_WEEKDAY = 1;
    final static int TUESDAY_WEEKDAY = 2;
    final static int WEDNESDAY_WEEKDAY = 3;
    final static int THURSDAY_WEEKDAY = 4;
    final static int FRIDAY_WEEKDAY = 5;
    final static int SATURDAY_WEEKDAY = 6;
    final static int SUNDAY_WEEKDAY = 7;

    static DateTime getDate(int weekday, int hour, int minute) {
        Integer[] fakeDate = getDate(weekday);
        assert fakeDate != null;
        return new DateTime(fakeDate[0], fakeDate[1], fakeDate[2], hour, minute);

    }

    static int[] getHourMinuteByTimeString(String time) {
        time = time.trim().replace(":", "").replace(" ", "");
        int hour;
        if (time.substring(0, 0).equals("0")) {
            hour = Integer.valueOf(time.substring(1, 1));
        } else {
            hour = Integer.valueOf(time.substring(0, 2));
        }
        int minute;
        if (time.substring(2, 2).equals("0")) {
            minute = Integer.valueOf(time.substring(3, 3));
        } else {
            minute = Integer.valueOf(time.substring(2, 4));
        }
        return new int[]{
                hour, minute
        };
    }

    static int getWeekdayByWeekdayName(String weekday) {
        String weekdayName = weekday.substring(0, 3).toLowerCase();
        Log.i("getweekdaybyweekdayneam", weekdayName);
        switch (weekdayName) {
            case "mon":
                return MONDAY_WEEKDAY;
            case "tue":
                return TUESDAY_WEEKDAY;
            case "wed":
                return WEDNESDAY_WEEKDAY;
            case "thu":
                return THURSDAY_WEEKDAY;
            case "fri":
                return FRIDAY_WEEKDAY;
            case "sat":
                return SATURDAY_WEEKDAY;
            case "sun":
                return SUNDAY_WEEKDAY;
            default:
                return 0;
        }
    }

    private static Integer[] getDate(int weekday) {
        final Integer[] SUNDAY_DATE = {
                2017, 3, 12
        };
        final Integer[] MONDAY_DATE = {
                2017, 3, 13
        };
        final Integer[] TUESDAY_DATE = {
                2017, 3, 14
        };
        final Integer[] WEDNESDAY_DATE = {
                2017, 3, 15
        };
        final Integer[] THURSDAY_DATE = {
                2017, 3, 16
        };
        final Integer[] FRIDAY_DATE = {
                2017, 3, 17
        };
        final Integer[] SATURDAY_DATE = {
                2017, 3, 18
        };
        switch (weekday) {
            case 1:
                return MONDAY_DATE;
            case 2:
                return TUESDAY_DATE;
            case 3:
                return WEDNESDAY_DATE;
            case 4:
                return THURSDAY_DATE;
            case 5:
                return FRIDAY_DATE;
            case 6:
                return SATURDAY_DATE;
            case 7:
                return SUNDAY_DATE;
            default:
                return null;
        }
    }

    /**
     * Return a PeriodFormatter using pattern HH:MM
     * for example 1:00'
     * This method should be called when formatting a duration
     */
    static PeriodFormatter durationFormatter = new PeriodFormatterBuilder()
            .appendHours()
            .appendSuffix(":")
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSuffix("'")
            .toFormatter();

    /**
     * Return a DateTimeFormatter using pattern HH:MM
     * for example 14:00
     * This method should be called twice when setting an availablePeriod since there are startTime and endTime for
     * each availablePeriod
     */
    static DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .appendLiteral(":")
            .appendMinuteOfHour(2)
            .toFormatter();


}

