package com.usiu.communityservice.data.repositories;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.data.models.CourseSettings;
import com.usiu.communityservice.data.models.NotificationItem;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.util.Constants;

import java.util.List;

public class SettingsAndAuditRepository {
    private static SettingsAndAuditRepository instance;
    private final FirebaseFirestore db;

    public interface ItemCallback<T> {
        void onSuccess(T item);
        void onError(Exception e);
    }

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public SettingsAndAuditRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized SettingsAndAuditRepository getInstance() {
        if (instance == null) {
            instance = new SettingsAndAuditRepository();
        }
        return instance;
    }

    // ================= COURSE SETTINGS =================

    public void getCourseSettings(ItemCallback<CourseSettings> callback) {
        DocumentReference ref = db.collection(Constants.COLL_COURSE_SETTINGS).document("default");
        ref.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                CourseSettings settings = doc.toObject(CourseSettings.class);
                callback.onSuccess(settings != null ? settings : new CourseSettings());
            } else {
                CourseSettings defaultSettings = new CourseSettings();
                ref.set(defaultSettings);
                callback.onSuccess(defaultSettings);
            }
        }).addOnFailureListener(callback::onError);
    }

    public void saveCourseSettings(CourseSettings settings, String coordinatorUid, ActionCallback callback) {
        settings.setUpdatedBy(coordinatorUid);
        settings.setUpdatedAt(Timestamp.now());

        db.collection(Constants.COLL_COURSE_SETTINGS)
                .document("default")
                .set(settings)
                .addOnSuccessListener(aVoid -> {
                    AuditLog log = new AuditLog(
                            "SETTINGS_UPDATE",
                            coordinatorUid,
                            "Coordinator",
                            Constants.ROLE_COORDINATOR,
                            "default",
                            "Updated CMS 3700 M semester parameters and deadlines"
                    );
                    db.collection(Constants.COLL_AUDIT_LOGS).add(log);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    // ================= AUDIT LOGS =================

    public void getAuditLogs(ListCallback<AuditLog> callback) {
        db.collection(Constants.COLL_AUDIT_LOGS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(AuditLog.class)))
                .addOnFailureListener(callback::onError);
    }

    // ================= USER & ROLE MANAGEMENT (ADMIN & COORDINATOR) =================

    public void getAllUsers(ListCallback<User> callback) {
        db.collection(Constants.COLL_USERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(User.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getUserById(String uid, ItemCallback<User> callback) {
        db.collection(Constants.COLL_USERS).document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess(doc.toObject(User.class));
                    } else {
                        callback.onError(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateUserRoleAndStatus(String targetUid, String newRole, boolean active,
                                        String adminUid, String adminName, ActionCallback callback) {
        DocumentReference userRef = db.collection(Constants.COLL_USERS).document(targetUid);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        userRef.update("role", newRole, "active", active)
                .addOnSuccessListener(aVoid -> {
                    AuditLog log = new AuditLog(
                            "ROLE_CHANGE",
                            adminUid,
                            adminName,
                            Constants.ROLE_ADMIN,
                            targetUid,
                            "Changed role to " + newRole + " (Active: " + active + ")"
                    );
                    auditRef.set(log);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    public void reviewStudentEligibility(String studentUid, String newStatus, String coordinatorUid,
                                         String coordinatorName, ActionCallback callback) {
        DocumentReference userRef = db.collection(Constants.COLL_USERS).document(studentUid);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        userRef.update("eligibilityStatus", newStatus)
                .addOnSuccessListener(aVoid -> {
                    AuditLog log = new AuditLog(
                            "ELIGIBILITY_DECISION",
                            coordinatorUid,
                            coordinatorName,
                            Constants.ROLE_COORDINATOR,
                            studentUid,
                            "Updated student eligibility to: " + newStatus
                    );
                    auditRef.set(log);

                    NotificationItem notif = new NotificationItem(
                            studentUid,
                            "Project Eligibility Decision",
                            "Your project-based community service eligibility status is now: " + newStatus,
                            "ELIGIBILITY"
                    );
                    db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onError);
    }

    // ================= NOTIFICATIONS =================

    public void getUserNotifications(String uid, ListCallback<NotificationItem> callback) {
        db.collection(Constants.COLL_NOTIFICATIONS)
                .whereEqualTo("recipientUid", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(NotificationItem.class)))
                .addOnFailureListener(callback::onError);
    }
}
