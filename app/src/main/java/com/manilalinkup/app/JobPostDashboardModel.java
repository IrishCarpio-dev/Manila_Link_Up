package com.manilalinkup.app;

import java.util.Objects;

public class JobPostDashboardModel {
    String jobTitle, employerName, jobPostLocation, job_duration,howLongJobIsPosted;
    int employerProfilePicture;

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

    public int getEmployerProfilePicture() {
        return employerProfilePicture;
    }

    public String getHowLongJobIsPosted() {
        return howLongJobIsPosted;
    }

    public JobPostDashboardModel(String jobTitle, String employerName, String jobPostLocation, String job_duration, int employerProfilePicture ,String howLongJobIsPosted) {
        this.jobTitle = jobTitle;
        this.employerName = employerName;
        this.jobPostLocation = jobPostLocation;
        this.job_duration = job_duration;
        this.employerProfilePicture = employerProfilePicture;
        this.howLongJobIsPosted = howLongJobIsPosted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobPostDashboardModel that = (JobPostDashboardModel) o;
        return Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(employerName, that.employerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobTitle, employerName);
    }
}
