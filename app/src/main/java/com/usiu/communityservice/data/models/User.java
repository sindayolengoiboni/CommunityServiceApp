package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String fullName;
    private String email;
    private String role; // STUDENT, SUPERVISOR, COORDINATOR, LECTURER, ADMIN
    private String studentId;
    private String phone;
    private String studentStatus; // FULL_TIME, PART_TIME, WORKING
    private String serviceOption; // HANDS_ON, PROJECT_BASED
    private String eligibilityStatus; // PENDING, APPROVED, REJECTED, CORRECTION_NEEDED
    private String employmentProofUrl;
    private String organizationId; // For supervisors
    private boolean active;
    private double recordedHours;
    private double verifiedHours;
    private Timestamp createdAt;

    public User() {
        // Firestore required
    }

    public User(String uid, String fullName, String email, String role) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = true;
        this.createdAt = Timestamp.now();
        this.recordedHours = 0.0;
        this.verifiedHours = 0.0;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStudentStatus() { return studentStatus; }
    public void setStudentStatus(String studentStatus) { this.studentStatus = studentStatus; }

    public String getServiceOption() { return serviceOption; }
    public void setServiceOption(String serviceOption) { this.serviceOption = serviceOption; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getEmploymentProofUrl() { return employmentProofUrl; }
    public void setEmploymentProofUrl(String employmentProofUrl) { this.employmentProofUrl = employmentProofUrl; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getRecordedHours() { return recordedHours; }
    public void setRecordedHours(double recordedHours) { this.recordedHours = recordedHours; }

    public double getVerifiedHours() { return verifiedHours; }
    public void setVerifiedHours(double verifiedHours) { this.verifiedHours = verifiedHours; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
