package com.manilalinkup.app;

public class EmployerChatModel {

    int seekerImage;
    String seekerName;
    String messagePreview;
    String messageTimeStamp;
    String uid;

    public EmployerChatModel(int seekerImage, String seekerName, String messagePreview, String messageTimeStamp, String uid) {
        this.seekerImage = seekerImage;
        this.seekerName = seekerName;
        this.messagePreview = messagePreview;
        this.messageTimeStamp = messageTimeStamp;
        this.uid = uid;
    }

    public int getSeekerImage() {
        return seekerImage;
    }

    public void setSeekerImage(int seekerImage) {
        this.seekerImage = seekerImage;
    }

    public String getSeekerName() {
        return seekerName;
    }

    public void setSeekerName(String seekerName) {
        this.seekerName = seekerName;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(String messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
