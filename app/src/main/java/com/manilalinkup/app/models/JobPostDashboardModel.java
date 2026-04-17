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
}