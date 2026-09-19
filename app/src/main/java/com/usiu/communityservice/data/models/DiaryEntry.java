package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import com.usiu.communityservice.util.Constants;
import java.io.Serializable;

public class DiaryEntry implements Serializable {
    private String id;
    private String studentUid;
    private String studentName;
    private String studentIdNumber;
    private String organizationId;
    private String organizationName;
    private String date; // YYYY-MM-DD
    private String arrivalTime; // HH:mm
    private String departureTime; // HH:mm
    private String activitiesPerformed;
    private String meetingsOrEvents;
    private double hoursRecorded;
    private String studentComments;
    private String verificationStatus; // PENDING, VERIFIED, FLAGGED
    private String supervisorComments;
    private String coordinatorComments;
    private String verifiedByUid;
    private String verifiedByName;
    private Timestamp createdAt;
    private Timestamp verifiedAt;

    public DiaryEntry() {
        this.verificationStatus = Constants.DIARY_PENDING;
        this.createdAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getActivitiesPerformed() { return activitiesPerformed; }
    public void setActivitiesPerformed(String activitiesPerformed) { this.activitiesPerformed = activitiesPerformed; }

    public String getMeetingsOrEvents() { return meetingsOrEvents; }
    public void setMeetingsOrEvents(String meetingsOrEvents) { this.meetingsOrEvents = meetingsOrEvents; }

    public double getHoursRecorded() { return hoursRecorded; }
    public void setHoursRecorded(double hoursRecorded) { this.hoursRecorded = hoursRecorded; }

    public String getStudentComments() { return studentComments; }
    public void setStudentComments(String studentComments) { this.studentComments = studentComments; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getSupervisorComments() { return supervisorComments; }
    public void setSupervisorComments(String supervisorComments) { this.supervisorComments = supervisorComments; }

    public String getCoordinatorComments() { return coordinatorComments; }
    public void setCoordinatorComments(String coordinatorComments) { this.coordinatorComments = coordinatorComments; }

    public String getVerifiedByUid() { return verifiedByUid; }
    public void setVerifiedByUid(String verifiedByUid) { this.verifiedByUid = verifiedByUid; }

    public String getVerifiedByName() { return verifiedByName; }
    public void setVerifiedByName(String verifiedByName) { this.verifiedByName = verifiedByName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Timestamp verifiedAt) { this.verifiedAt = verifiedAt; }
}
