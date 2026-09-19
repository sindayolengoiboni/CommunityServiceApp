package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import com.usiu.communityservice.util.Constants;
import java.io.Serializable;

public class AcademicReport implements Serializable {
    private String id;
    private String studentUid;
    private String studentName;
    private String studentIdNumber;
    private String serviceOption; // HANDS_ON, PROJECT_BASED
    private String reportType; // FIRST_IMPRESSIONS, FINAL_REPORT, SITE_EVALUATION, PROJECT_PROPOSAL, PROJECT_FINAL
    private String title;
    private String documentUrl;
    private String documentFileName;
    private String status; // DRAFT, SUBMITTED, UNDER_REVIEW, CORRECTION_REQUIRED, APPROVED, REJECTED, LATE
    private String gradeOrScore;
    private String lecturerFeedback;
    private String reviewedByLecturerUid;
    private String reviewedByLecturerName;
    private Timestamp submittedAt;
    private Timestamp reviewedAt;

    public AcademicReport() {
        this.status = Constants.REPORT_STATUS_SUBMITTED;
        this.submittedAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getServiceOption() { return serviceOption; }
    public void setServiceOption(String serviceOption) { this.serviceOption = serviceOption; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }

    public String getDocumentFileName() { return documentFileName; }
    public void setDocumentFileName(String documentFileName) { this.documentFileName = documentFileName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGradeOrScore() { return gradeOrScore; }
    public void setGradeOrScore(String gradeOrScore) { this.gradeOrScore = gradeOrScore; }

    public String getLecturerFeedback() { return lecturerFeedback; }
    public void setLecturerFeedback(String lecturerFeedback) { this.lecturerFeedback = lecturerFeedback; }

    public String getReviewedByLecturerUid() { return reviewedByLecturerUid; }
    public void setReviewedByLecturerUid(String reviewedByLecturerUid) { this.reviewedByLecturerUid = reviewedByLecturerUid; }

    public String getReviewedByLecturerName() { return reviewedByLecturerName; }
    public void setReviewedByLecturerName(String reviewedByLecturerName) { this.reviewedByLecturerName = reviewedByLecturerName; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public Timestamp getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Timestamp reviewedAt) { this.reviewedAt = reviewedAt; }
}
