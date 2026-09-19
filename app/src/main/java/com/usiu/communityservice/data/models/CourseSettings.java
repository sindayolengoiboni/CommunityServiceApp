package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import com.usiu.communityservice.util.Constants;
import java.io.Serializable;

public class CourseSettings implements Serializable {
    private String id;
    private String semesterName;
    private Timestamp semesterStartDate;
    private int registrationDeadlineWeeks;
    private double handsOnMinHours;
    private int handsOnDaysPerWeek;
    private double handsOnHoursPerDay;
    private int handsOnDurationWeeks;
    private int maxAllowedAbsencesSite;
    private int maxAllowedAbsencesClass;
    private boolean enforceInstitutionalEmail;
    private String updatedBy;
    private Timestamp updatedAt;

    public CourseSettings() {
        this.id = "default";
        this.semesterName = "Current Semester";
        this.semesterStartDate = Timestamp.now();
        this.registrationDeadlineWeeks = Constants.DEFAULT_REGISTRATION_DEADLINE_WEEKS;
        this.handsOnMinHours = Constants.DEFAULT_MIN_HOURS;
        this.handsOnDaysPerWeek = Constants.DEFAULT_DAYS_PER_WEEK;
        this.handsOnHoursPerDay = Constants.DEFAULT_HOURS_PER_DAY;
        this.handsOnDurationWeeks = Constants.DEFAULT_DURATION_WEEKS;
        this.maxAllowedAbsencesSite = Constants.DEFAULT_MAX_ALLOWED_ABSENCES_SITE;
        this.maxAllowedAbsencesClass = Constants.DEFAULT_MAX_ALLOWED_ABSENCES_CLASS;
        this.enforceInstitutionalEmail = true;
        this.updatedAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public Timestamp getSemesterStartDate() { return semesterStartDate; }
    public void setSemesterStartDate(Timestamp semesterStartDate) { this.semesterStartDate = semesterStartDate; }

    public int getRegistrationDeadlineWeeks() { return registrationDeadlineWeeks; }
    public void setRegistrationDeadlineWeeks(int registrationDeadlineWeeks) { this.registrationDeadlineWeeks = registrationDeadlineWeeks; }

    public double getHandsOnMinHours() { return handsOnMinHours; }
    public void setHandsOnMinHours(double handsOnMinHours) { this.handsOnMinHours = handsOnMinHours; }

    public int getHandsOnDaysPerWeek() { return handsOnDaysPerWeek; }
    public void setHandsOnDaysPerWeek(int handsOnDaysPerWeek) { this.handsOnDaysPerWeek = handsOnDaysPerWeek; }

    public double getHandsOnHoursPerDay() { return handsOnHoursPerDay; }
    public void setHandsOnHoursPerDay(double handsOnHoursPerDay) { this.handsOnHoursPerDay = handsOnHoursPerDay; }

    public int getHandsOnDurationWeeks() { return handsOnDurationWeeks; }
    public void setHandsOnDurationWeeks(int handsOnDurationWeeks) { this.handsOnDurationWeeks = handsOnDurationWeeks; }

    public int getMaxAllowedAbsencesSite() { return maxAllowedAbsencesSite; }
    public void setMaxAllowedAbsencesSite(int maxAllowedAbsencesSite) { this.maxAllowedAbsencesSite = maxAllowedAbsencesSite; }

    public int getMaxAllowedAbsencesClass() { return maxAllowedAbsencesClass; }
    public void setMaxAllowedAbsencesClass(int maxAllowedAbsencesClass) { this.maxAllowedAbsencesClass = maxAllowedAbsencesClass; }

    public boolean isEnforceInstitutionalEmail() { return enforceInstitutionalEmail; }
    public void setEnforceInstitutionalEmail(boolean enforceInstitutionalEmail) { this.enforceInstitutionalEmail = enforceInstitutionalEmail; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
