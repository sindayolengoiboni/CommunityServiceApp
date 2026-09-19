package com.usiu.communityservice.data.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SupervisionVisit implements Serializable {
    private String id;
    private String coordinatorUid;
    private String coordinatorName;
    private String organizationId;
    private String organizationName;
    private Timestamp visitDate;
    private List<String> observedStudentUids = new ArrayList<>();
    private List<String> observedStudentNames = new ArrayList<>();
    private String activitiesObserved;
    private String attendanceObservations;
    private String organizationFeedback;
    private String challengesNoted;
    private String followUpActions;
    private Timestamp nextVisitDate;
    private Timestamp createdAt;

    public SupervisionVisit() {
        this.visitDate = Timestamp.now();
        this.createdAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCoordinatorUid() { return coordinatorUid; }
    public void setCoordinatorUid(String coordinatorUid) { this.coordinatorUid = coordinatorUid; }

    public String getCoordinatorName() { return coordinatorName; }
    public void setCoordinatorName(String coordinatorName) { this.coordinatorName = coordinatorName; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public Timestamp getVisitDate() { return visitDate; }
    public void setVisitDate(Timestamp visitDate) { this.visitDate = visitDate; }

    public List<String> getObservedStudentUids() { return observedStudentUids; }
    public void setObservedStudentUids(List<String> observedStudentUids) { this.observedStudentUids = observedStudentUids; }

    public List<String> getObservedStudentNames() { return observedStudentNames; }
    public void setObservedStudentNames(List<String> observedStudentNames) { this.observedStudentNames = observedStudentNames; }

    public String getActivitiesObserved() { return activitiesObserved; }
    public void setActivitiesObserved(String activitiesObserved) { this.activitiesObserved = activitiesObserved; }

    public String getAttendanceObservations() { return attendanceObservations; }
    public void setAttendanceObservations(String attendanceObservations) { this.attendanceObservations = attendanceObservations; }

    public String getOrganizationFeedback() { return organizationFeedback; }
    public void setOrganizationFeedback(String organizationFeedback) { this.organizationFeedback = organizationFeedback; }

    public String getChallengesNoted() { return challengesNoted; }
    public void setChallengesNoted(String challengesNoted) { this.challengesNoted = challengesNoted; }

    public String getFollowUpActions() { return followUpActions; }
    public void setFollowUpActions(String followUpActions) { this.followUpActions = followUpActions; }

    public Timestamp getNextVisitDate() { return nextVisitDate; }
    public void setNextVisitDate(Timestamp nextVisitDate) { this.nextVisitDate = nextVisitDate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
