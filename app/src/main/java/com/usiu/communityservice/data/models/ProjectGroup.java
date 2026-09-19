package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectGroup implements Serializable {
    private String id;
    private String title;
    private String description;
    private String communityPartner;
    private List<String> memberUids = new ArrayList<>();
    private List<String> memberNames = new ArrayList<>();
    private List<String> milestones = new ArrayList<>();
    private String handoverNotes;
    private String status; // PENDING_APPROVAL, ACTIVE, COMPLETED
    private Timestamp createdAt;

    public ProjectGroup() {
        this.status = "PENDING_APPROVAL";
        this.createdAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCommunityPartner() { return communityPartner; }
    public void setCommunityPartner(String communityPartner) { this.communityPartner = communityPartner; }

    public List<String> getMemberUids() { return memberUids; }
    public void setMemberUids(List<String> memberUids) { this.memberUids = memberUids; }

    public List<String> getMemberNames() { return memberNames; }
    public void setMemberNames(List<String> memberNames) { this.memberNames = memberNames; }

    public List<String> getMilestones() { return milestones; }
    public void setMilestones(List<String> milestones) { this.milestones = milestones; }

    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
