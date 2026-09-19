package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class AuditLog implements Serializable {
    private String id;
    private String actionType; // APPROVAL_DECISION, REJECTED_APPLICATION, ATTENDANCE_CHANGE, HOUR_CHANGE, REPORT_STATUS_CHANGE, ROLE_CHANGE, SUPERVISION_RECORD
    private String performedByUid;
    private String performedByName;
    private String performedByRole;
    private String targetEntityId;
    private String details;
    private Timestamp timestamp;

    public AuditLog() {
        this.timestamp = Timestamp.now();
    }

    public AuditLog(String actionType, String performedByUid, String performedByName, String performedByRole, String targetEntityId, String details) {
        this.actionType = actionType;
        this.performedByUid = performedByUid;
        this.performedByName = performedByName;
        this.performedByRole = performedByRole;
        this.targetEntityId = targetEntityId;
        this.details = details;
        this.timestamp = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getPerformedByUid() { return performedByUid; }
    public void setPerformedByUid(String performedByUid) { this.performedByUid = performedByUid; }

    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }

    public String getPerformedByRole() { return performedByRole; }
    public void setPerformedByRole(String performedByRole) { this.performedByRole = performedByRole; }

    public String getTargetEntityId() { return targetEntityId; }
    public void setTargetEntityId(String targetEntityId) { this.targetEntityId = targetEntityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
