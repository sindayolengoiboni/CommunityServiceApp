package com.usiu.communityservice.data.repositories;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.usiu.communityservice.data.models.AcademicReport;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.data.models.NotificationItem;
import com.usiu.communityservice.util.Constants;

import java.util.List;

public class ReportRepository {
    private static ReportRepository instance;
    private final FirebaseFirestore db;

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private ReportRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized ReportRepository getInstance() {
        if (instance == null) {
            instance = new ReportRepository();
        }
        return instance;
    }

    public void submitReport(AcademicReport report, ActionCallback callback) {
        DocumentReference ref = db.collection(Constants.COLL_REPORTS).document();
        report.setId(ref.getId());
        report.setSubmittedAt(Timestamp.now());
        report.setStatus(Constants.REPORT_STATUS_SUBMITTED);

        ref.set(report)
                .addOnSuccessListener(aVoid -> {
                    NotificationItem notif = new NotificationItem(
                            report.getStudentUid(),
                            "Report Uploaded",
                            "Your " + report.getReportType().replace("_", " ") + " has been submitted for lecturer evaluation.",
                            "REPORT"
                    );
                    db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    public void getStudentReports(String studentUid, ListCallback<AcademicReport> callback) {
        db.collection(Constants.COLL_REPORTS)
                .whereEqualTo("studentUid", studentUid)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(AcademicReport.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getAllReportsForReview(ListCallback<AcademicReport> callback) {
        db.collection(Constants.COLL_REPORTS)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(AcademicReport.class)))
                .addOnFailureListener(callback::onError);
    }

    public void reviewReport(String reportId, String newStatus, String score, String feedback,
                             String lecturerUid, String lecturerName, ActionCallback callback) {
        DocumentReference reportRef = db.collection(Constants.COLL_REPORTS).document(reportId);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        reportRef.get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                callback.onError(new Exception("Report not found"));
                return;
            }
            AcademicReport report = doc.toObject(AcademicReport.class);
            if (report == null) return;

            reportRef.update(
                    "status", newStatus,
                    "gradeOrScore", score,
                    "lecturerFeedback", feedback,
                    "reviewedByLecturerUid", lecturerUid,
                    "reviewedByLecturerName", lecturerName,
                    "reviewedAt", Timestamp.now()
            ).addOnSuccessListener(aVoid -> {
                AuditLog log = new AuditLog(
                        "REPORT_STATUS_CHANGE",
                        lecturerUid,
                        lecturerName,
                        Constants.ROLE_LECTURER,
                        reportId,
                        "Lecturer graded report (" + report.getReportType() + ") for " + report.getStudentName() + ": " + newStatus
                );
                auditRef.set(log);

                NotificationItem notif = new NotificationItem(
                        report.getStudentUid(),
                        "Report Feedback Available",
                        "Your " + report.getReportType().replace("_", " ") + " status updated to " + newStatus + ". Feedback: " + feedback,
                        "REPORT"
                );
                db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                callback.onSuccess();
            }).addOnFailureListener(callback::onError);
        }).addOnFailureListener(callback::onError);
    }
}
