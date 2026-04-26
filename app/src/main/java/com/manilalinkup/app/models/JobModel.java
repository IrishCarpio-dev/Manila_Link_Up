package com.manilalinkup.app.models;

import java.util.List;

public class JobModel {
    String id, title, description, expiresAt, duration, location, createdAt, filledAt;
    Double salary;
    List<String> tags;
    EmployerProfileModel employer;
    boolean hasApplied;
    ApplicantModel hiredApplication;

    public JobModel() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getExpiresAt() { return expiresAt; }
    public String getDuration() { return duration; }
    public String getLocation() { return location; }
    public Double getSalary() { return salary; }
    public List<String> getTags() { return tags; }
    public EmployerProfileModel getEmployer() { return employer; }
    public String getCreatedAt() { return createdAt; }
    public String getFilledAt() { return filledAt; }
    public boolean isHasApplied() { return hasApplied; }
    public ApplicantModel getHiredApplication() { return hiredApplication; }
}
