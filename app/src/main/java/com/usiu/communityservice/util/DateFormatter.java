package com.usiu.communityservice.util;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateFormatter {
    private DateFormatter() {}

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_PATTERN = "MMM dd, yyyy";
    private static final String DISPLAY_DATETIME_PATTERN = "MMM dd, yyyy HH:mm";

    public static String formatDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(date);
    }

    public static String formatDisplayDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.getDefault()).format(date);
    }

    public static String formatDisplayDate(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return formatDisplayDate(timestamp.toDate());
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATETIME_PATTERN, Locale.getDefault()).format(timestamp.toDate());
    }

    public static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getTodayString() {
        return formatDate(new Date());
    }

    public static long getDifferenceInDays(Date start, Date end) {
        if (start == null || end == null) return 0;
        long diff = end.getTime() - start.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }
}
