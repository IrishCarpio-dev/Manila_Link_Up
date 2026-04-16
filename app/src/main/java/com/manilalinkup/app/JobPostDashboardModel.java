package com.manilalinkup.app;

import android.content.Intent;

public class JobPostDashboardModel {
    public static final String EXTRA_JOB_TITLE = "extra_job_title";
    public static final String EXTRA_EMPLOYER_NAME = "extra_employer_name";
    public static final String EXTRA_JOB_LOCATION = "extra_job_location";
    public static final String EXTRA_JOB_DURATION = "extra_job_duration";
    public static final String EXTRA_JOB_POSTED_AGO = "extra_job_posted_ago";
    public static final String EXTRA_EMPLOYER_PICTURE = "extra_employer_picture";

    private final String jobTitle;
    private final String employerName;
    private final String jobPostLocation;
    private final String jobDuration;
    private final String howLongJobIsPosted;
    private final int employerProfilePicture;

    public JobPostDashboardModel(
            String jobTitle,
            String employerName,
            String jobPostLocation,
            String jobDuration,
            int employerProfilePicture,
            String howLongJobIsPosted
    ) {
        this.jobTitle = jobTitle;
        this.employerName = employerName;
        this.jobPostLocation = jobPostLocation;
        this.jobDuration = jobDuration;
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

    public String getJobDuration() {
        return jobDuration;
    }

    public int getEmployerProfilePicture() {
        return employerProfilePicture;
    }

    public String getHowLongJobIsPosted() {
        return howLongJobIsPosted;
    }

    public void putIntoIntent(Intent intent) {
        intent.putExtra(EXTRA_JOB_TITLE, jobTitle);
        intent.putExtra(EXTRA_EMPLOYER_NAME, employerName);
        intent.putExtra(EXTRA_JOB_LOCATION, jobPostLocation);
        intent.putExtra(EXTRA_JOB_DURATION, jobDuration);
        intent.putExtra(EXTRA_JOB_POSTED_AGO, howLongJobIsPosted);
        intent.putExtra(EXTRA_EMPLOYER_PICTURE, employerProfilePicture);
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
                intent.getIntExtra(EXTRA_EMPLOYER_PICTURE, 0),
                intent.getStringExtra(EXTRA_JOB_POSTED_AGO)
        );
    }
}
