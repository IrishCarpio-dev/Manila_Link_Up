package com.manilalinkup.app.models;

public class JobPostDashboardModel {
    String jobTitle, employerName, jobPostLocation, job_duration,howLongJobIsPosted;
    String employerProfilePicture;

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

}