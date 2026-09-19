package com.usiu.communityservice.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public final class ValidationUtils {
    private ValidationUtils() {}

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[0-9]{5,8}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isInstitutionalEmail(String email) {
        if (!isValidEmail(email)) return false;
        // Check USIU domain or subdomains
        String lower = email.toLowerCase().trim();
        return lower.endsWith(Constants.INSTITUTION_EMAIL_DOMAIN) || lower.endsWith(".usiu.ac.ke");
    }

    public static boolean isValidStudentId(String studentId) {
        if (TextUtils.isEmpty(studentId)) return false;
        return STUDENT_ID_PATTERN.matcher(studentId.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        return phone.trim().length() >= 9 && phone.trim().length() <= 15;
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidTimeFormat(String time) {
        if (TextUtils.isEmpty(time)) return false;
        return TIME_PATTERN.matcher(time.trim()).matches();
    }

    public static boolean isValidDailyHours(double hours) {
        return hours > 0.0 && hours <= 12.0;
    }
}
