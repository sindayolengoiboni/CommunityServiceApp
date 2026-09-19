package com.usiu.communityservice.data.repositories;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.usiu.communityservice.data.models.Application;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.data.models.NotificationItem;
import com.usiu.communityservice.data.models.Opportunity;
import com.usiu.communityservice.util.Constants;

import java.util.List;

public class ApplicationRepository {
    private static ApplicationRepository instance;
    private final FirebaseFirestore db;

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private ApplicationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized ApplicationRepository getInstance() {
        if (instance == null) {
            instance = new ApplicationRepository();
        }
        return instance;
    }

    public void submitApplication(Application application, ActionCallback callback) {
        DocumentReference ref = db.collection(Constants.COLL_APPLICATIONS).document();
        application.setId(ref.getId());
        application.setAppliedAt(Timestamp.now());
        application.setStatus(Constants.APP_STATUS_PENDING);

        ref.set(application)
                .addOnSuccessListener(aVoid -> {
                    // Send notification to student
                    NotificationItem notif = new NotificationItem(
                            application.getStudentUid(),
                            "Application Submitted",
                            "Your application for " + application.getOpportunityTitle() + " at " + application.getOrganizationName() + " has been submitted.",
                            "APPLICATION"
                    );
                    db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    public void getStudentApplications(String studentUid, ListCallback<Application> callback) {
        db.collection(Constants.COLL_APPLICATIONS)
                .whereEqualTo("studentUid", studentUid)
                .orderBy("appliedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Application.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getOrganizationApplications(String organizationId, ListCallback<Application> callback) {
        db.collection(Constants.COLL_APPLICATIONS)
                .whereEqualTo("organizationId", organizationId)
                .orderBy("appliedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Application.class)))
                .addOnFailureListener(callback::onError);
    }

    /**
     * Approves an application atomically preventing overbooking capacity.
     */
    public void approveApplicationWithCapacityCheck(String applicationId, String opportunityId,
                                                   String supervisorUid, String supervisorName,
                                                   ActionCallback callback) {
        DocumentReference appRef = db.collection(Constants.COLL_APPLICATIONS).document(applicationId);
        DocumentReference oppRef = db.collection(Constants.COLL_OPPORTUNITIES).document(opportunityId);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        db.runTransaction(transaction -> {
            Opportunity opp = transaction.get(oppRef).toObject(Opportunity.class);
            Application app = transaction.get(appRef).toObject(Application.class);

            if (opp == null || app == null) {
                throw new IllegalStateException("Opportunity or application record not found.");
            }

            if (opp.getFilledSlots() >= opp.getTotalSlots()) {
                throw new IllegalStateException("Placement capacity is full. Cannot approve application.");
            }

            // Increment filled slots
            int newFilled = opp.getFilledSlots() + 1;
            transaction.update(oppRef, "filledSlots", newFilled);

            // Update application status
            transaction.update(appRef,
                    "status", Constants.APP_STATUS_APPROVED,
                    "supervisorUid", supervisorUid,
                    "decisionAt", Timestamp.now());

            // Write audit log
            AuditLog log = new AuditLog(
                    "PLACEMENT_APPROVAL",
                    supervisorUid,
                    supervisorName,
                    Constants.ROLE_SUPERVISOR,
                    applicationId,
                    "Approved placement application for " + app.getStudentName() + " (" + app.getOpportunityTitle() + ")"
            );
            transaction.set(auditRef, log);

            return app;
        }).addOnSuccessListener(app -> {
            // Notify student
            NotificationItem notif = new NotificationItem(
                    app.getStudentUid(),
                    "Placement Approved!",
                    "Your placement application for " + app.getOpportunityTitle() + " has been approved by the organization. Please proceed with official site registration.",
                    "APPLICATION"
            );
            db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
            callback.onSuccess();
        }).addOnFailureListener(callback::onError);
    }

    public void rejectApplication(String applicationId, String supervisorUid, String supervisorName,
                                  String reason, ActionCallback callback) {
        DocumentReference appRef = db.collection(Constants.COLL_APPLICATIONS).document(applicationId);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        appRef.get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                callback.onError(new Exception("Application not found"));
                return;
            }
            Application app = doc.toObject(Application.class);
            if (app == null) return;

            appRef.update(
                    "status", Constants.APP_STATUS_REJECTED,
                    "supervisorUid", supervisorUid,
                    "decisionNotes", reason,
                    "decisionAt", Timestamp.now()
            ).addOnSuccessListener(aVoid -> {
                AuditLog log = new AuditLog(
                        "PLACEMENT_REJECTION",
                        supervisorUid,
                        supervisorName,
                        Constants.ROLE_SUPERVISOR,
                        applicationId,
                        "Rejected application for " + app.getStudentName() + ". Reason: " + reason
                );
                auditRef.set(log);

                NotificationItem notif = new NotificationItem(
                        app.getStudentUid(),
                        "Placement Application Update",
                        "Your placement application for " + app.getOpportunityTitle() + " was not approved. Reason: " + reason,
                        "APPLICATION"
                );
                db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                callback.onSuccess();
            }).addOnFailureListener(callback::onError);
        }).addOnFailureListener(callback::onError);
    }
}
