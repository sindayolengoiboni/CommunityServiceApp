package com.usiu.communityservice.data.repositories;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.usiu.communityservice.data.models.AttendanceRecord;
import com.usiu.communityservice.data.models.AuditLog;
import com.usiu.communityservice.data.models.DiaryEntry;
import com.usiu.communityservice.data.models.NotificationItem;
import com.usiu.communityservice.data.models.User;
import com.usiu.communityservice.util.Constants;

import java.util.List;

public class ServiceTrackingRepository {
    private static ServiceTrackingRepository instance;
    private final FirebaseFirestore db;

    public interface ListCallback<T> {
        void onSuccess(List<T> items);
        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public interface AbsenceStatsCallback {
        void onStats(int siteAbsences, int classAbsences, boolean siteWarning, boolean classWarning);
        void onError(Exception e);
    }

    private ServiceTrackingRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized ServiceTrackingRepository getInstance() {
        if (instance == null) {
            instance = new ServiceTrackingRepository();
        }
        return instance;
    }

    // ================= DIGITAL DIARY =================

    public void addDiaryEntry(DiaryEntry entry, ActionCallback callback) {
        DocumentReference diaryRef = db.collection(Constants.COLL_DIARY_ENTRIES).document();
        entry.setId(diaryRef.getId());
        entry.setVerificationStatus(Constants.DIARY_PENDING);
        entry.setCreatedAt(Timestamp.now());

        DocumentReference userRef = db.collection(Constants.COLL_USERS).document(entry.getStudentUid());

        db.runTransaction(transaction -> {
            User student = transaction.get(userRef).toObject(User.class);
            if (student != null) {
                double newRecorded = student.getRecordedHours() + entry.getHoursRecorded();
                transaction.update(userRef, "recordedHours", newRecorded);
            }
            transaction.set(diaryRef, entry);
            return null;
        }).addOnSuccessListener(aVoid -> callback.onSuccess())
          .addOnFailureListener(callback::onError);
    }

    public void getStudentDiaryEntries(String studentUid, ListCallback<DiaryEntry> callback) {
        db.collection(Constants.COLL_DIARY_ENTRIES)
                .whereEqualTo("studentUid", studentUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(DiaryEntry.class)))
                .addOnFailureListener(callback::onError);
    }

    public void getOrganizationDiaryEntries(String organizationId, ListCallback<DiaryEntry> callback) {
        db.collection(Constants.COLL_DIARY_ENTRIES)
                .whereEqualTo("organizationId", organizationId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(DiaryEntry.class)))
                .addOnFailureListener(callback::onError);
    }

    public void verifyDiaryEntry(String entryId, String supervisorUid, String supervisorName,
                                 String comments, ActionCallback callback) {
        DocumentReference diaryRef = db.collection(Constants.COLL_DIARY_ENTRIES).document(entryId);
        DocumentReference auditRef = db.collection(Constants.COLL_AUDIT_LOGS).document();

        db.runTransaction(transaction -> {
            DiaryEntry entry = transaction.get(diaryRef).toObject(DiaryEntry.class);
            if (entry == null) throw new IllegalStateException("Diary entry not found");

            DocumentReference userRef = db.collection(Constants.COLL_USERS).document(entry.getStudentUid());
            User student = transaction.get(userRef).toObject(User.class);

            double hours = entry.getHoursRecorded();
            if (student != null) {
                double newVerified = student.getVerifiedHours() + hours;
                transaction.update(userRef, "verifiedHours", newVerified);
            }

            transaction.update(diaryRef,
                    "verificationStatus", Constants.DIARY_VERIFIED,
                    "supervisorComments", comments,
                    "verifiedByUid", supervisorUid,
                    "verifiedByName", supervisorName,
                    "verifiedAt", Timestamp.now());

            AuditLog log = new AuditLog(
                    "DIARY_VERIFICATION",
                    supervisorUid,
                    supervisorName,
                    Constants.ROLE_SUPERVISOR,
                    entryId,
                    "Verified " + hours + " hours for student " + entry.getStudentName()
            );
            transaction.set(auditRef, log);

            return entry;
        }).addOnSuccessListener(entry -> {
            NotificationItem notif = new NotificationItem(
                    entry.getStudentUid(),
                    "Diary Entry Verified",
                    "Your supervisor verified " + entry.getHoursRecorded() + " hours for date " + entry.getDate() + ".",
                    "DIARY"
            );
            db.collection(Constants.COLL_NOTIFICATIONS).add(notif);
            callback.onSuccess();
        }).addOnFailureListener(callback::onError);
    }

    public void flagDiaryEntry(String entryId, String supervisorUid, String supervisorName,
                               String comments, ActionCallback callback) {
        DocumentReference diaryRef = db.collection(Constants.COLL_DIARY_ENTRIES).document(entryId);
        diaryRef.update(
                "verificationStatus", Constants.DIARY_FLAGGED,
                "supervisorComments", comments,
                "verifiedByUid", supervisorUid,
                "verifiedByName", supervisorName,
                "verifiedAt", Timestamp.now()
        ).addOnSuccessListener(aVoid -> callback.onSuccess())
         .addOnFailureListener(callback::onError);
    }

    // ================= ATTENDANCE =================

    public void recordAttendance(AttendanceRecord record, ActionCallback callback) {
        DocumentReference ref = db.collection(Constants.COLL_ATTENDANCE).document();
        record.setId(ref.getId());
        record.setTimestamp(Timestamp.now());

        ref.set(record)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    public void getStudentAttendance(String studentUid, ListCallback<AttendanceRecord> callback) {
        db.collection(Constants.COLL_ATTENDANCE)
                .whereEqualTo("studentUid", studentUid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> callback.onSuccess(snap.toObjects(AttendanceRecord.class)))
                .addOnFailureListener(callback::onError);
    }

    public void checkAbsenceStatistics(String studentUid, int maxSite, int maxClass, AbsenceStatsCallback callback) {
        db.collection(Constants.COLL_ATTENDANCE)
                .whereEqualTo("studentUid", studentUid)
                .whereEqualTo("status", Constants.ATT_STATUS_ABSENT)
                .get()
                .addOnSuccessListener(snap -> {
                    int siteAbsences = 0;
                    int classAbsences = 0;

                    for (AttendanceRecord record : snap.toObjects(AttendanceRecord.class)) {
                        if (Constants.ATT_TYPE_SITE.equals(record.getType())) {
                            siteAbsences++;
                        } else if (Constants.ATT_TYPE_CLASS.equals(record.getType())) {
                            classAbsences++;
                        }
                    }

                    boolean siteWarning = siteAbsences >= maxSite;
                    boolean classWarning = classAbsences >= maxClass;

                    callback.onStats(siteAbsences, classAbsences, siteWarning, classWarning);
                })
                .addOnFailureListener(callback::onError);
    }
}
