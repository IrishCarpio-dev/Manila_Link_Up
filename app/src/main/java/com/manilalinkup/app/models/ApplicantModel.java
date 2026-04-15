package com.manilalinkup.app.models;

public class ApplicantModel {
    String id;
    String seekerUid;
    Integer status;
    SeekerProfileModel seeker;

    public ApplicantModel(String id, String seekerUid, Integer status, SeekerProfileModel seeker) {
        this.id = id;
        this.seekerUid = seekerUid;
        this.status = status;
        this.seeker = seeker;
    }

    public String getId() { return id; }
    public String getSeekerUid() { return seekerUid; }
    public Integer getStatus() { return status; }
    public SeekerProfileModel getSeeker() { return seeker; }
}
