package com.manilalinkup.app.models;

public class JobPostDashboardModel {
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

    public String getJobDuration() {
        return job_duration;
    }

    public String getEmployerProfilePicture() {
        return employerProfilePicture;
    }

    public String getHowLongJobIsPosted() {
        return howLongJobIsPosted;
    }

    public void putIntoIntent(android.content.Intent intent) {
        intent.putExtra("jobTitle", jobTitle);
        intent.putExtra("employerName", employerName);
        intent.putExtra("jobPostLocation", jobPostLocation);
        intent.putExtra("job_duration", job_duration);
        intent.putExtra("employerProfilePicture", employerProfilePicture);
        intent.putExtra("howLongJobIsPosted", howLongJobIsPosted);
    }

    public static JobPostDashboardModel fromIntent(android.content.Intent intent) {
        if (intent == null || !intent.hasExtra("jobTitle")) {
            return null;
        }
        return new JobPostDashboardModel(
                intent.getStringExtra("jobTitle"),
                intent.getStringExtra("employerName"),
                intent.getStringExtra("jobPostLocation"),
                intent.getStringExtra("job_duration"),
                intent.getStringExtra("employerProfilePicture"),
                intent.getStringExtra("howLongJobIsPosted")
        );
    }
}
