package com.manilalinkup.app;

import java.util.Objects;

public class SeekerJobModel {
    private final String jobTitle;
    private final String employerName;
    private final String jobLocation;
    private final String jobDuration;
    private final String timePosted;
    private final int employerProfilePicture;

    public SeekerJobModel(String jobTitle, String employerName, String jobLocation, String jobDuration, String timePosted, int employerProfilePicture) {
        this.jobTitle = jobTitle;
        this.employerName = employerName;
        this.jobLocation = jobLocation;
        this.jobDuration = jobDuration;
        this.timePosted = timePosted;
        this.employerProfilePicture = employerProfilePicture;
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
