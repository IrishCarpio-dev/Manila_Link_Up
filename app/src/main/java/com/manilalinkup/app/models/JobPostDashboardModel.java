package com.manilalinkup.app.models;

import java.util.List;

public class JobPostDashboardModel {
    String jobId;
    String jobTitle, employerName, jobPostLocation, job_duration,howLongJobIsPosted;
    String employerProfilePicture;
    List<String> tagIds;

    public JobPostDashboardModel(String jobTitle, String employerName, String jobPostLocation, String job_duration, String employerProfilePicture ,String howLongJobIsPosted) {
        this.jobTitle = jobTitle;
        this.employerName = employerName;
        this.jobPostLocation = jobPostLocation;
        this.job_duration = job_duration;
        this.employerProfilePicture = employerProfilePicture;
        this.howLongJobIsPosted = howLongJobIsPosted;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getJobPostLocation() {
        return jobPostLocation;
    }

    public String getJob_duration() {
        return job_duration;
    }

    public String getEmployerProfilePicture() {
        return employerProfilePicture;
    }

    public String getHowLongJobIsPosted() {
        return howLongJobIsPosted;
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public List<String> getTagIds() { return tagIds; }
    public void setTagIds(List<String> tagIds) { this.tagIds = tagIds; }

    private Double salary;
    private String description;
    private String expiresAt;

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    private boolean hasApplied;
    public boolean isHasApplied() { return hasApplied; }
    public void setHasApplied(boolean hasApplied) { this.hasApplied = hasApplied; }

    private String applicationId;
    private Integer applicationStatus;
    private boolean employerHasCompleted;
    private String seekerName;

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public Integer getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(Integer applicationStatus) { this.applicationStatus = applicationStatus; }

    public boolean isEmployerHasCompleted() { return employerHasCompleted; }
    public void setEmployerHasCompleted(boolean employerHasCompleted) { this.employerHasCompleted = employerHasCompleted; }

    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }
}