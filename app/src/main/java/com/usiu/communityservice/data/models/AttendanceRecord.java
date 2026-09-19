package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import com.usiu.communityservice.util.Constants;
import java.io.Serializable;

public class AttendanceRecord implements Serializable {
    private String id;
    private String studentUid;
    private String studentName;
    private String studentIdNumber;
    private String type; // SITE, IN_CLASS, PROJECT
    private String date; // YYYY-MM-DD
    private String status; // PRESENT, ABSENT, LATE, EXCUSED
    private String notes;
    private boolean verified;
    private String recordedByUid;
    private String recordedByName;
    private String recordedByRole;
    private Timestamp timestamp;

    public AttendanceRecord() {
        this.timestamp = Timestamp.now();
        this.status = Constants.ATT_STATUS_PRESENT;
        this.verified = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getRecordedByUid() { return recordedByUid; }
    public void setRecordedByUid(String recordedByUid) { this.recordedByUid = recordedByUid; }

    public String getRecordedByName() { return recordedByName; }
    public void setRecordedByName(String recordedByName) { this.recordedByName = recordedByName; }

    public String getRecordedByRole() { return recordedByRole; }
    public void setRecordedByRole(String recordedByRole) { this.recordedByRole = recordedByRole; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
