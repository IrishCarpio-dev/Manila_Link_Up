package com.manilalinkup.app.models;

public class ApplicationModel {
    String id;
    String seekerUid;
    String jobId;
    Integer status;
    String chatId;
    String updatedAt;
    String createdAt;
    String employerCompletedAt;
    String seekerCompletedAt;

    public ApplicationModel() {}

    public String getId() { return id; }
    public String getSeekerUid() { return seekerUid; }
    public String getJobId() { return jobId; }
    public Integer getStatus() { return status; }
    public String getChatId() { return chatId; }
    public String getUpdatedAt() { return updatedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getEmployerCompletedAt() { return employerCompletedAt; }
    public String getSeekerCompletedAt() { return seekerCompletedAt; }
}
