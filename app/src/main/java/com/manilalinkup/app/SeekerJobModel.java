package com.manilalinkup.app;

import java.util.Objects;

public class SeekerJobModel {
    private String jobTitle;
    private String employerName;
    private String jobLocation;
    private String jobDuration;
    private String timePosted;
    private int employerProfilePicture;

    public SeekerJobModel(String jobTitle, String employerName, String jobLocation, String jobDuration, int employerProfilePicture, String timePosted) {
        this.jobTitle = jobTitle;
        this.employerName = employerName;
        this.jobLocation = jobLocation;
        this.jobDuration = jobDuration;
        this.employerProfilePicture = employerProfilePicture;
        this.timePosted = timePosted;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getJobDuration() {
        return jobDuration;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public int getEmployerProfilePicture() {
        return employerProfilePicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeekerJobModel that = (SeekerJobModel) o;
        return employerProfilePicture == that.employerProfilePicture &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(employerName, that.employerName) &&
                Objects.equals(jobLocation, that.jobLocation) &&
                Objects.equals(jobDuration, that.jobDuration) &&
                Objects.equals(timePosted, that.timePosted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobTitle, employerName, jobLocation, jobDuration, timePosted, employerProfilePicture);
    }
}
