package com.manilalinkup.app.models;

public class AppliedJobModel {
    String id;
    String jobId;
    Integer status;
    JobModel job;

    public AppliedJobModel(String id, String jobId, Integer status, JobModel job) {
        this.id = id;
        this.jobId = jobId;
        this.status = status;
        this.job = job;
    }

    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public Integer getStatus() { return status; }
    public JobModel getJob() { return job; }
}
