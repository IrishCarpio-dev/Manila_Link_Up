package com.manilalinkup.app.models;

public class ArchiveJobModel {
    String jobId; // Unique ID for database actions
    String jobTitle;
    String statusText;
    String insightText;

    public ArchiveJobModel(String jobId, String jobTitle, String statusText, String insightText) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.statusText = statusText;
        this.insightText = insightText;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getInsightText() {
        return insightText;
    }
}
