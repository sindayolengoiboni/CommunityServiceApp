package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import com.usiu.communityservice.util.Constants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Registration implements Serializable {
    private String id;
    private String applicationId;
    private String studentUid;
    private String studentName;
    private String studentIdNumber;
    private String organizationId;
    private String organizationName;
    private String supervisorName;
    private String supervisorEmail;
    private String supervisorPhone;
    private List<String> serviceDays = new ArrayList<>();
    private String serviceStartTime;
    private String serviceEndTime;
    private Timestamp startDate;
    private Timestamp endDate;
    private String serviceOption;
    private String status; // SUBMITTED, APPROVED_BY_COORDINATOR, REJECTED_BY_COORDINATOR
    private String coordinatorNotes;
    private boolean lateSubmission;
    private Timestamp submittedAt;
    private Timestamp reviewedAt;
    private String reviewedByCoordinatorUid;

    public Registration() {
        this.status = Constants.REG_STATUS_SUBMITTED;
        this.submittedAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

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

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public String getSupervisorEmail() { return supervisorEmail; }
    public void setSupervisorEmail(String supervisorEmail) { this.supervisorEmail = supervisorEmail; }

    public String getSupervisorPhone() { return supervisorPhone; }
    public void setSupervisorPhone(String supervisorPhone) { this.supervisorPhone = supervisorPhone; }

    public List<String> getServiceDays() { return serviceDays; }
    public void setServiceDays(List<String> serviceDays) { this.serviceDays = serviceDays; }

    public String getServiceStartTime() { return serviceStartTime; }
    public void setServiceStartTime(String serviceStartTime) { this.serviceStartTime = serviceStartTime; }

    public String getServiceEndTime() { return serviceEndTime; }
    public void setServiceEndTime(String serviceEndTime) { this.serviceEndTime = serviceEndTime; }

    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(Timestamp startDate) { this.startDate = startDate; }

    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(Timestamp endDate) { this.endDate = endDate; }

    public String getServiceOption() { return serviceOption; }
    public void setServiceOption(String serviceOption) { this.serviceOption = serviceOption; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCoordinatorNotes() { return coordinatorNotes; }
    public void setCoordinatorNotes(String coordinatorNotes) { this.coordinatorNotes = coordinatorNotes; }

    public boolean isLateSubmission() { return lateSubmission; }
    public void setLateSubmission(boolean lateSubmission) { this.lateSubmission = lateSubmission; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public Timestamp getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Timestamp reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getReviewedByCoordinatorUid() { return reviewedByCoordinatorUid; }
    public void setReviewedByCoordinatorUid(String reviewedByCoordinatorUid) { this.reviewedByCoordinatorUid = reviewedByCoordinatorUid; }
}
