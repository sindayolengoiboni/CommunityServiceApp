package com.usiu.communityservice.data.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Organization implements Serializable {
    private String id;
    private String name;
    private String type; // NGO, SCHOOL, HOSPITAL, COMMUNITY_CENTER, etc.
    private String location;
    private String description;
    private String communityServed;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private int totalCapacity;
    private int filledSlots;
    private List<String> availableDays = new ArrayList<>();
    private String serviceTime; // e.g. "09:00 - 12:00"
    private String supportedOption; // HANDS_ON, PROJECT_BASED, BOTH
    private boolean approved;
    private boolean active;
    private String coordinatorComments;

    public Organization() {
        this.approved = false;
        this.active = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCommunityServed() { return communityServed; }
    public void setCommunityServed(String communityServed) { this.communityServed = communityServed; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getFilledSlots() { return filledSlots; }
    public void setFilledSlots(int filledSlots) { this.filledSlots = filledSlots; }

    public int getAvailableSlots() { return Math.max(0, totalCapacity - filledSlots); }

    public List<String> getAvailableDays() { return availableDays; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }

    public String getServiceTime() { return serviceTime; }
    public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }

    public String getSupportedOption() { return supportedOption; }
    public void setSupportedOption(String supportedOption) { this.supportedOption = supportedOption; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCoordinatorComments() { return coordinatorComments; }
    public void setCoordinatorComments(String coordinatorComments) { this.coordinatorComments = coordinatorComments; }
}
