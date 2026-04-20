package com.manilalinkup.app.models;

public class ApplicantModel {
    String id;
    String seekerUid;
    String jobId;
    Integer status;
    String chatId;
    SeekerProfileModel seeker;

    public ApplicantModel() {}

    public String getId() { return id; }
    public String getSeekerUid() { return seekerUid; }
    public String getJobId() { return jobId; }
    public Integer getStatus() { return status; }
    public String getChatId() { return chatId; }
    public SeekerProfileModel getSeeker() { return seeker; }
}
