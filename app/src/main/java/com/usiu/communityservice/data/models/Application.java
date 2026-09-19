package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import com.usiu.communityservice.util.Constants;
import java.io.Serializable;

public class Application implements Serializable {
    private String id;
    private String studentUid;
    private String studentName;
    private String studentIdNumber;
    private String studentEmail;
    private String opportunityId;
    private String opportunityTitle;
    private String organizationId;
    private String organizationName;
    private String serviceOption;
    private String status; // PENDING, APPROVED_BY_ORG, REJECTED_BY_ORG, CANCELLED
    private String decisionNotes;
    private String supervisorUid;
    private Timestamp appliedAt;
    private Timestamp decisionAt;

    public Application() {
        this.status = Constants.APP_STATUS_PENDING;
        this.appliedAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getOpportunityTitle() { return opportunityTitle; }
    public void setOpportunityTitle(String opportunityTitle) { this.opportunityTitle = opportunityTitle; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getServiceOption() { return serviceOption; }
    public void setServiceOption(String serviceOption) { this.serviceOption = serviceOption; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDecisionNotes() { return decisionNotes; }
    public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }

    public String getSupervisorUid() { return supervisorUid; }
    public void setSupervisorUid(String supervisorUid) { this.supervisorUid = supervisorUid; }

    public Timestamp getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }

    public Timestamp getDecisionAt() { return decisionAt; }
    public void setDecisionAt(Timestamp decisionAt) { this.decisionAt = decisionAt; }
}
