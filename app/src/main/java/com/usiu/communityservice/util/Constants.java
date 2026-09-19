package com.usiu.communityservice.util;

public final class Constants {
    private Constants() {}

    // Firestore Collections
    public static final String COLL_USERS = "users";
    public static final String COLL_COURSE_SETTINGS = "courseSettings";
    public static final String COLL_ORGANIZATIONS = "organizations";
    public static final String COLL_OPPORTUNITIES = "opportunities";
    public static final String COLL_APPLICATIONS = "applications";
    public static final String COLL_REGISTRATIONS = "registrations";
    public static final String COLL_DIARY_ENTRIES = "diaryEntries";
    public static final String COLL_ATTENDANCE = "attendance";
    public static final String COLL_SUPERVISION_VISITS = "supervisionVisits";
    public static final String COLL_REPORTS = "reports";
    public static final String COLL_PROJECT_GROUPS = "projectGroups";
    public static final String COLL_NOTIFICATIONS = "notifications";
    public static final String COLL_AUDIT_LOGS = "auditLogs";

    // User Roles
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_SUPERVISOR = "SUPERVISOR";
    public static final String ROLE_COORDINATOR = "COORDINATOR";
    public static final String ROLE_LECTURER = "LECTURER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Service Tracks
    public static final String OPTION_HANDS_ON = "HANDS_ON";
    public static final String OPTION_PROJECT = "PROJECT_BASED";

    // Student Status
    public static final String STATUS_FULL_TIME = "FULL_TIME";
    public static final String STATUS_PART_TIME = "PART_TIME";
    public static final String STATUS_WORKING = "WORKING";

    // Eligibility Status
    public static final String ELIGIBILITY_PENDING = "PENDING";
    public static final String ELIGIBILITY_APPROVED = "APPROVED";
    public static final String ELIGIBILITY_REJECTED = "REJECTED";
    public static final String ELIGIBILITY_CORRECTION_NEEDED = "CORRECTION_NEEDED";

    // Application Statuses
    public static final String APP_STATUS_PENDING = "PENDING";
    public static final String APP_STATUS_APPROVED = "APPROVED_BY_ORG";
    public static final String APP_STATUS_REJECTED = "REJECTED_BY_ORG";
    public static final String APP_STATUS_CANCELLED = "CANCELLED";

    // Registration Statuses
    public static final String REG_STATUS_SUBMITTED = "SUBMITTED";
    public static final String REG_STATUS_APPROVED = "APPROVED_BY_COORDINATOR";
    public static final String REG_STATUS_REJECTED = "REJECTED_BY_COORDINATOR";

    // Diary Verification Statuses
    public static final String DIARY_PENDING = "PENDING";
    public static final String DIARY_VERIFIED = "VERIFIED";
    public static final String DIARY_FLAGGED = "FLAGGED";

    // Attendance Types & Statuses
    public static final String ATT_TYPE_SITE = "SITE";
    public static final String ATT_TYPE_CLASS = "IN_CLASS";
    public static final String ATT_TYPE_PROJECT = "PROJECT";

    public static final String ATT_STATUS_PRESENT = "PRESENT";
    public static final String ATT_STATUS_ABSENT = "ABSENT";
    public static final String ATT_STATUS_LATE = "LATE";
    public static final String ATT_STATUS_EXCUSED = "EXCUSED";

    // Report Types & Statuses
    public static final String REPORT_FIRST_IMPRESSIONS = "FIRST_IMPRESSIONS";
    public static final String REPORT_FINAL = "FINAL_REPORT";
    public static final String REPORT_SITE_EVALUATION = "SITE_EVALUATION";
    public static final String REPORT_PROJECT_PROPOSAL = "PROJECT_PROPOSAL";
    public static final String REPORT_PROJECT_FINAL = "PROJECT_FINAL";

    public static final String REPORT_STATUS_DRAFT = "DRAFT";
    public static final String REPORT_STATUS_SUBMITTED = "SUBMITTED";
    public static final String REPORT_STATUS_UNDER_REVIEW = "UNDER_REVIEW";
    public static final String REPORT_STATUS_CORRECTION_REQUIRED = "CORRECTION_REQUIRED";
    public static final String REPORT_STATUS_APPROVED = "APPROVED";
    public static final String REPORT_STATUS_REJECTED = "REJECTED";

    // Course Defaults (CMS 3700 M)
    public static final double DEFAULT_MIN_HOURS = 90.0;
    public static final int DEFAULT_DAYS_PER_WEEK = 3;
    public static final double DEFAULT_HOURS_PER_DAY = 3.0;
    public static final int DEFAULT_DURATION_WEEKS = 10;
    public static final int DEFAULT_REGISTRATION_DEADLINE_WEEKS = 3;
    public static final int DEFAULT_MAX_ALLOWED_ABSENCES_SITE = 2;
    public static final int DEFAULT_MAX_ALLOWED_ABSENCES_CLASS = 2;

    // Institution Domain
    public static final String INSTITUTION_EMAIL_DOMAIN = "@usiu.ac.ke";
}
