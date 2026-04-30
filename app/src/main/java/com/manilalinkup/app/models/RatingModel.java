package com.manilalinkup.app.models;

public class RatingModel {
    String id;
    String jobId;
    String applicationId;
    String raterUid;
    String raterRole;
    String rateeUid;
    String rateeRole;
    int score;
    String comment;
    String createdAt;
    String updatedAt;
    RaterInfo rater;
    JobInfo job;

    public RatingModel() {}

    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public String getApplicationId() { return applicationId; }
    public String getRaterUid() { return raterUid; }
    public String getRaterRole() { return raterRole; }
    public String getRateeUid() { return rateeUid; }
    public String getRateeRole() { return rateeRole; }
    public int getScore() { return score; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public RaterInfo getRater() { return rater; }
    public JobInfo getJob() { return job; }

    public static class RaterInfo {
        String uid, name, profilePhoto;
        public String getUid() { return uid; }
        public String getName() { return name; }
        public String getProfilePhoto() { return profilePhoto; }
    }

    public static class JobInfo {
        String id, title;
        public String getId() { return id; }
        public String getTitle() { return title; }
    }
}
