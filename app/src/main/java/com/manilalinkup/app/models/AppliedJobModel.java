package com.manilalinkup.app.models;

public class AppliedJobModel {
    String id;
    String jobId;
    Integer status;
    JobWithEmployerModel job;

    public AppliedJobModel(String id, String jobId, Integer status, JobWithEmployerModel job) {
        this.id = id;
        this.jobId = jobId;
        this.status = status;
        this.job = job;
    }

    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public Integer getStatus() { return status; }
    public JobWithEmployerModel getJob() { return job; }
}
