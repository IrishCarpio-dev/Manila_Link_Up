package com.manilalinkup.app.models;

import android.content.Intent;

public class JobPostDashboardModel {
    public static final String EXTRA_JOB_TITLE = "job_title";
    public static final String EXTRA_EMPLOYER_NAME = "employer_name";
    public static final String EXTRA_JOB_LOCATION = "job_location";
    public static final String EXTRA_JOB_DURATION = "job_duration";
    public static final String EXTRA_EMPLOYER_PROFILE_PICTURE = "employer_profile_picture";
    public static final String EXTRA_POSTED_AGO = "posted_ago";

    String jobTitle, employerName, jobPostLocation, job_duration, howLongJobIsPosted;
    String employerProfilePicture;

    public JobPostDashboardModel(String jobTitle, String employerName, String jobPostLocation, String job_duration, String employerProfilePicture, String howLongJobIsPosted) {
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

    public void putIntoIntent(Intent intent) {
        intent.putExtra(EXTRA_JOB_TITLE, jobTitle);
        intent.putExtra(EXTRA_EMPLOYER_NAME, employerName);
        intent.putExtra(EXTRA_JOB_LOCATION, jobPostLocation);
        intent.putExtra(EXTRA_JOB_DURATION, job_duration);
        intent.putExtra(EXTRA_EMPLOYER_PROFILE_PICTURE, employerProfilePicture);
        intent.putExtra(EXTRA_POSTED_AGO, howLongJobIsPosted);
    }

    public static JobPostDashboardModel fromIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_JOB_TITLE)) {
            return null;
        }

        return new JobPostDashboardModel(
                intent.getStringExtra(EXTRA_JOB_TITLE),
                intent.getStringExtra(EXTRA_EMPLOYER_NAME),
                intent.getStringExtra(EXTRA_JOB_LOCATION),
                intent.getStringExtra(EXTRA_JOB_DURATION),
                intent.getStringExtra(EXTRA_EMPLOYER_PROFILE_PICTURE),
                intent.getStringExtra(EXTRA_POSTED_AGO)
        );
    }
}
