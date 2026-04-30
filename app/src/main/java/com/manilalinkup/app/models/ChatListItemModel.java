package com.manilalinkup.app.models;

public class ChatListItemModel {
    String id;
    String jobId;
    String seekerUid;
    String employerUid;
    String applicationId;
    Integer applicationStatus;
    String lastMessage;
    String lastMessageAt;
    int unreadCount;
    CounterpartModel counterpart;
    JobSummaryModel job;

    public ChatListItemModel() {}

    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public String getSeekerUid() { return seekerUid; }
    public String getEmployerUid() { return employerUid; }
    public String getApplicationId() { return applicationId; }
    public Integer getApplicationStatus() { return applicationStatus; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageAt() { return lastMessageAt; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastMessageAt(String lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public CounterpartModel getCounterpart() { return counterpart; }
    public JobSummaryModel getJob() { return job; }

    public static class CounterpartModel {
        String uid, name, profilePhoto;
        public String getUid() { return uid; }
        public String getName() { return name; }
        public String getProfilePhoto() { return profilePhoto; }
    }

    public static class JobSummaryModel {
        String id, title;
        public String getId() { return id; }
        public String getTitle() { return title; }
    }
}
