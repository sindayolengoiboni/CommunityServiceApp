package com.usiu.communityservice.data.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Opportunity implements Serializable {
    private String id;
    private String organizationId;
    private String organizationName;
    private String title;
    private String description;
    private String location;
    private List<String> serviceDays = new ArrayList<>();
    private String startTime;
    private String endTime;
    private String serviceOption; // HANDS_ON, PROJECT_BASED
    private int totalSlots;
    private int filledSlots;
    private boolean active;

    public Opportunity() {
        this.active = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getServiceDays() { return serviceDays; }
    public void setServiceDays(List<String> serviceDays) { this.serviceDays = serviceDays; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getServiceOption() { return serviceOption; }
    public void setServiceOption(String serviceOption) { this.serviceOption = serviceOption; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public int getFilledSlots() { return filledSlots; }
    public void setFilledSlots(int filledSlots) { this.filledSlots = filledSlots; }

    public int getAvailableSlots() { return Math.max(0, totalSlots - filledSlots); }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
