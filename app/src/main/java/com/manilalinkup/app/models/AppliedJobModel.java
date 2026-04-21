package com.manilalinkup.app.models;

public class AppliedJobModel {
    String id;
    String jobId;
    String seekerUid;
    Integer status;
    String chatId;
    String createdAt;
    String updatedAt;
    String employerCompletedAt;
    String seekerCompletedAt;
    JobModel job;

    public AppliedJobModel() {}

    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public String getSeekerUid() { return seekerUid; }
    public Integer getStatus() { return status; }
    public String getChatId() { return chatId; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getEmployerCompletedAt() { return employerCompletedAt; }
    public String getSeekerCompletedAt() { return seekerCompletedAt; }
    public JobModel getJob() { return job; }
}
