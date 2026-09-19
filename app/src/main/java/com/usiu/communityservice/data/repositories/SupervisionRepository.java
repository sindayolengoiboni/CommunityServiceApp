package com.usiu.communityservice.data.repositories;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.data.models.NotificationItem;
import com.usiu.communityservice.data.models.Registration;
import com.usiu.communityservice.data.models.SupervisionVisit;
import com.usiu.communityservice.util.Constants;

import java.util.List;

public class SupervisionRepository {
    private static SupervisionRepository instance;
    private final FirebaseFirestore db;

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private SupervisionRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized SupervisionRepository getInstance() {
        if (instance == null) {
            instance = new SupervisionRepository();
        }
        return instance;
    }

    // ================= SITE REGISTRATIONS =================

    public void submitRegistration(Registration registration, ActionCallback callback) {
        DocumentReference ref = db.collection(Constants.COLL_REGISTRATIONS).document();
        registration.setId(ref.getId());
        registration.setSubmittedAt(Timestamp.now());
        registration.setStatus(Constants.REG_STATUS_SUBMITTED);

        ref.set(registration)
                .addOnSuccessListener(aVoid -> {
                    NotificationItem notif = new NotificationItem(
                            registration.getStudentUid(),
                            "Site Registration Submitted",
                            "Official site registration for " + registration.getOrganizationName() + " submitted to Coordinator for review.",
                            "REGISTRATION"
                    );
                    db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    public void getStudentRegistration(String studentUid, ListCallback<Registration> callback) {
        db.collection(Constants.COLL_REGISTRATIONS)
                .whereEqualTo("studentUid", studentUid)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Registration.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getAllRegistrations(ListCallback<Registration> callback) {
        db.collection(Constants.COLL_REGISTRATIONS)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(Registration.class)))
                .addOnFailureListener(callback::onError);
    }

    public void reviewRegistration(String regId, boolean approved, String coordinatorUid,
                                   String coordinatorName, String comments, ActionCallback callback) {
        DocumentReference regRef = db.collection(Constants.COLL_REGISTRATIONS).document(regId);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        String newStatus = approved ? Constants.REG_STATUS_APPROVED : Constants.REG_STATUS_REJECTED;

        regRef.get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                callback.onError(new Exception("Registration record not found"));
                return;
            }
            Registration reg = doc.toObject(Registration.class);
            if (reg == null) return;

            regRef.update(
                    "status", newStatus,
                    "coordinatorNotes", comments,
                    "reviewedByCoordinatorUid", coordinatorUid,
                    "reviewedAt", Timestamp.now()
            ).addOnSuccessListener(aVoid -> {
                AuditLog log = new AuditLog(
                        "REGISTRATION_DECISION",
                        coordinatorUid,
                        coordinatorName,
                        Constants.ROLE_COORDINATOR,
                        regId,
                        (approved ? "Approved" : "Rejected") + " site registration for " + reg.getStudentName()
                );
                auditRef.set(log);

                NotificationItem notif = new NotificationItem(
                        reg.getStudentUid(),
                        "Site Registration " + (approved ? "Approved" : "Rejected"),
                        "Coordinator decision: " + (approved ? "Your official site registration has been approved!" : "Your registration was rejected. Reason: " + comments),
                        "REGISTRATION"
                );
                db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                callback.onSuccess();
            }).addOnFailureListener(callback::onError);
        }).addOnFailureListener(callback::onError);
    }

    // ================= PHYSICAL SUPERVISION VISITS =================

    public void recordSupervisionVisit(SupervisionVisit visit, ActionCallback callback) {
        DocumentReference ref = db.collection(Constants.COLL_SUPERVISION_VISITS).document();
        visit.setId(ref.getId());
        visit.setCreatedAt(Timestamp.now());

        ref.set(visit)
                .addOnSuccessListener(aVoid -> {
                    AuditLog log = new AuditLog(
                            "SUPERVISION_RECORD",
                            visit.getCoordinatorUid(),
                            visit.getCoordinatorName(),
                            Constants.ROLE_COORDINATOR,
                            visit.getId(),
                            "Logged physical supervision visit at " + visit.getOrganizationName()
                    );
                    db.collection(Constants.COLL_AUDIT_LOGS).add(log);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllSupervisionVisits(ListCallback<SupervisionVisit> callback) {
        db.collection(Constants.COLL_SUPERVISION_VISITS)
                .orderBy("visitDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(SupervisionVisit.class)))
                .addOnFailureListener(callback::onError);
    }
}
