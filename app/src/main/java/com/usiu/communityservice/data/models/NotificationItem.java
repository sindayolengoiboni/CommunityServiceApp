package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class NotificationItem implements Serializable {
    private String id;
    private String recipientUid;
    private String title;
    private String message;
    private String type; // APPLICATION, REGISTRATION, ATTENDANCE_WARNING, REPORT, GENERAL
    private boolean read;
    private Timestamp createdAt;

    public NotificationItem() {
        this.read = false;
        this.createdAt = Timestamp.now();
    }

    public NotificationItem(String recipientUid, String title, String message, String type) {
        this.recipientUid = recipientUid;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = false;
        this.createdAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientUid() { return recipientUid; }
    public void setRecipientUid(String recipientUid) { this.recipientUid = recipientUid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
